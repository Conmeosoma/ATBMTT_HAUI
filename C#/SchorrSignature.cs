using System.Numerics;

namespace Schnorr
{
    public class SchorrSignature
    {
        public BigInteger S { get; }
        public BigInteger E { get; }

        public SchorrSignature(BigInteger s, BigInteger e)
        {
            S = s;
            E = e;
        }

        public override string ToString()
        {
            var sHex = S.ToString("X");
            var eHex = E.ToString("X");
            int max = 64;
            if (sHex.Length > max)
                sHex = sHex.Substring(0, max) + "...";
            if (eHex.Length > max)
                eHex = eHex.Substring(0, max) + "...";
            return $"SchorrSignature{{ s={sHex}, e={eHex} }}";
        }
    }
}
