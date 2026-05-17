using System;
using System.Numerics;
using System.Security.Cryptography;
using System.Text;

namespace Schnorr
{
    public class SchorrSignatureAlgorithm
    {
        private SchorrKeyPair keyPair;
        private readonly RandomNumberGenerator rng = RandomNumberGenerator.Create();

        public SchorrSignatureAlgorithm() { }

        public SchorrKeyPair GenerateKeys(int qBitLength = 256)
        {
            var parameters = SchorrSignatureParams.GenerateSystemParameters(qBitLength);
            BigInteger q = parameters.Q;

            BigInteger x = RandomInRange(BigInteger.One, q - 1);
            BigInteger y = BigInteger.ModPow(parameters.G, x, parameters.P);

            keyPair = new SchorrKeyPair(x, y, parameters);
            return keyPair;
        }

        public SchorrSignature Sign(string message)
        {
            if (keyPair == null)
                throw new InvalidOperationException(
                    "Key pair not generated. Call GenerateKeys() first."
                );
            var p = keyPair.Params.P;
            var q = keyPair.Params.Q;
            var g = keyPair.Params.G;
            var x = keyPair.PrivateKey;

            BigInteger k = RandomInRange(BigInteger.One, q - 1);
            BigInteger r = BigInteger.ModPow(g, k, p);
            BigInteger e = HashToBigInteger(message, r, q);
            BigInteger s = (k + e * x) % q;
            return new SchorrSignature(s, e);
        }

        public bool Verify(string message, SchorrSignature signature)
        {
            if (keyPair == null)
                throw new InvalidOperationException(
                    "Key pair not generated. Call GenerateKeys() first."
                );
            var p = keyPair.Params.P;
            var q = keyPair.Params.Q;
            var g = keyPair.Params.G;
            var y = keyPair.PublicKey;

            BigInteger s = signature.S;
            BigInteger e = signature.E;

            BigInteger gs = BigInteger.ModPow(g, s, p);
            // y^(-e) mod p == y^(q - e) mod p
            BigInteger negE = (q - (e % q)) % q;
            BigInteger yInvE = BigInteger.ModPow(y, negE, p);
            BigInteger rPrime = (gs * yInvE) % p;
            BigInteger ePrime = HashToBigInteger(message, rPrime, q);
            return ePrime == e;
        }

        private BigInteger HashToBigInteger(string message, BigInteger r, BigInteger q)
        {
            using var sha = SHA256.Create();
            byte[] msg = Encoding.UTF8.GetBytes(message);
            byte[] rBytes = ToBigEndian(r);
            byte[] input = new byte[msg.Length + rBytes.Length];
            Buffer.BlockCopy(msg, 0, input, 0, msg.Length);
            Buffer.BlockCopy(rBytes, 0, input, msg.Length, rBytes.Length);
            byte[] hash = sha.ComputeHash(input);
            BigInteger hv = new BigInteger(
                hash.Reverse().ToArray().Concat(new byte[] { 0 }).ToArray()
            );
            return hv % q;
        }

        private BigInteger RandomInRange(BigInteger minInclusive, BigInteger maxInclusive)
        {
            if (minInclusive > maxInclusive)
                throw new ArgumentException("min > max");
            BigInteger range = maxInclusive - minInclusive + 1;
            BigInteger r;
            do
            {
                r = RandomBigIntegerBelow(range);
            } while (r < 0 || r >= range);
            return minInclusive + r;
        }

        private BigInteger RandomBigIntegerBelow(BigInteger bound)
        {
            if (bound <= 0)
                return 0;
            int bits = BigIntegerExtensions.GetBitLengthExt(bound);
            int bytes = (bits + 7) / 8;
            byte[] b = new byte[bytes];
            BigInteger r;
            do
            {
                rng.GetBytes(b);
                // set top bit to limit size
                int topBits = bits - 8 * (bytes - 1);
                if (topBits < 8)
                {
                    int m = (1 << topBits) - 1;
                    byte mask = (byte)m;
                    b[0] &= mask;
                }
                r = new BigInteger(b.Reverse().ToArray().Concat(new byte[] { 0 }).ToArray());
            } while (r >= bound);
            return r;
        }

        private static byte[] ToBigEndian(BigInteger v)
        {
            if (v.IsZero)
                return new byte[] { 0 };
            var le = v.ToByteArray(); // little-endian two's complement
            // strip sign byte if present
            if (le[le.Length - 1] == 0)
            {
                le = le.Take(le.Length - 1).ToArray();
            }
            return le.Reverse().ToArray();
        }
    }
}
