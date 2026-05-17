using System.Numerics;

namespace Schnorr
{
    public class SchorrKeyPair
    {
        public BigInteger PrivateKey { get; }
        public BigInteger PublicKey { get; }
        public SchorrSignatureParams Params { get; }

        public SchorrKeyPair(
            BigInteger privateKey,
            BigInteger publicKey,
            SchorrSignatureParams parameters
        )
        {
            PrivateKey = privateKey;
            PublicKey = publicKey;
            Params = parameters;
        }

        public override string ToString()
        {
            string sx = PrivateKey.ToString("X");
            string sy = PublicKey.ToString("X");
            return $"SchorrKeyPair{{ x={sx.Substring(0, System.Math.Min(32, sx.Length))}..., y={sy.Substring(0, System.Math.Min(32, sy.Length))}... }}";
        }
    }
}
