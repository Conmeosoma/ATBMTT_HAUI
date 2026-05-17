using System;
using System.Windows.Forms;

namespace Schnorr
{
    static class SchorrMain
    {
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new SchorrForm());
        }
    }
}
