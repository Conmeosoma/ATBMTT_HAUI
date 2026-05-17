using System;
using System.Linq;
using System.Numerics;
using System.Security.Cryptography;

namespace Schnorr
{
    public class SchorrSignatureParams
    {
        public BigInteger P { get; }
        public BigInteger Q { get; }
        public BigInteger G { get; }

        private SchorrSignatureParams(BigInteger p, BigInteger q, BigInteger g)
        {
            P = p;
            Q = q;
            G = g;
        }

        public static SchorrSignatureParams GenerateSystemParameters(
            int qBitLength = 256,
            int millerRabinRounds = 40
        )
        {
            // Generate q (prime of qBitLength), then p = 2*q + 1 until p is prime
            BigInteger q = GenerateRandomPrime(qBitLength, millerRabinRounds);
            BigInteger p = q * 2 + 1;
            while (!IsProbablyPrime(p, millerRabinRounds))
            {
                q = GenerateRandomPrime(qBitLength, millerRabinRounds);
                p = q * 2 + 1;
            }

            // find generator g of subgroup of order q
            BigInteger g;
            var rng = RandomNumberGenerator.Create();
            BigInteger exponent = (p - 1) / q;
            do
            {
                BigInteger h = RandomBigIntegerBelow(p - 2, rng) + 2; // in [2, p-2]
                g = BigInteger.ModPow(h, exponent, p);
            } while (g == BigInteger.One);

            return new SchorrSignatureParams(p, q, g);
        }

        // Helpers
        private static BigInteger GenerateRandomPrime(int bits, int rounds)
        {
            var rng = RandomNumberGenerator.Create();
            while (true)
            {
                BigInteger candidate = RandomBigInteger(bits, rng);
                // make it odd
                if ((candidate & 1) == 0)
                    candidate |= 1;
                if (IsProbablyPrime(candidate, rounds))
                    return candidate;
            }
        }

        private static BigInteger RandomBigInteger(int bitLength, RandomNumberGenerator rng)
        {
            int byteLen = (bitLength + 7) / 8;
            byte[] bytes = new byte[byteLen];
            rng.GetBytes(bytes);
            // set top bit to ensure bit length
            int topBit = (bitLength - 1) % 8;
            bytes[0] |= (byte)(1 << topBit);
            // convert big-endian byte[] to little-endian with sign byte
            byte[] le = bytes.Reverse().ToArray().Concat(new byte[] { 0 }).ToArray();
            return new BigInteger(le);
        }

        private static BigInteger RandomBigIntegerBelow(BigInteger bound, RandomNumberGenerator rng)
        {
            if (bound <= 0)
                return 0;
            int bits = BigIntegerExtensions.GetBitLengthExt(bound);
            BigInteger r;
            do
            {
                r = RandomBigInteger(bits, rng);
            } while (r >= bound);
            return r;
        }

        private static bool IsProbablyPrime(BigInteger n, int k)
        {
            if (n < 2)
                return false;
            if (n % 2 == 0)
                return n == 2;

            BigInteger d = n - 1;
            int s = 0;
            while ((d & 1) == 0)
            {
                d >>= 1;
                s++;
            }

            var rng = RandomNumberGenerator.Create();
            byte[] bytes = new byte[(int)BigIntegerExtensions.GetByteCountExt(n)];

            for (int i = 0; i < k; i++)
            {
                BigInteger a = RandomBigIntegerBelow(n - 3, rng) + 2; // in [2, n-2]
                BigInteger x = BigInteger.ModPow(a, d, n);
                if (x == 1 || x == n - 1)
                    continue;
                bool composite = true;
                for (int r = 1; r < s; r++)
                {
                    x = BigInteger.ModPow(x, 2, n);
                    if (x == n - 1)
                    {
                        composite = false;
                        break;
                    }
                }
                if (composite)
                    return false;
            }
            return true;
        }
    }

    static class BigIntegerExtensions
    {
        public static int GetBitLengthExt(this BigInteger value)
        {
            if (value.IsZero) return 0;
            BigInteger v = value < 0 ? -value : value;
            int bits = 0;
            while (v > 0)
            {
                bits++;
                v >>= 1;
            }
            return bits;
        }

        public static int GetByteCountExt(this BigInteger value)
        {
            return (GetBitLengthExt(value) + 7) / 8;
        }
    }
}
