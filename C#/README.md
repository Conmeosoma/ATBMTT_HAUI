# Schnorr WinForms (C#)

This folder contains a WinForms port of the Schnorr signature demo (original in Java).

Prerequisites
- .NET SDK 6.0 or 7.0 installed

Run
1. Open a terminal in this folder:

```powershell
cd "C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin\C#"
```

2. Build and run with dotnet:

```powershell
dotnet run
```

What is included
- `SchnorrApp.csproj` - project file (WinForms)
- `SchorrMain.cs` - app entry (runs `SchorrForm`)
- `SchorrForm` implemented in `SchorrGUI.cs` - WinForms UI
- `SchorrSignatureParams.cs`, `SchorrKeyPair.cs`, `SchorrSignature.cs`, `SchorrSignatureAlgorithm.cs` - algorithm implementation

Notes
- Generating primes and keys may take several seconds.
- This implementation is for educational/demo purposes only. Do NOT use it in production.

If you want a Visual Studio designer-based WinForms project, tell me and I will generate `.Designer.cs` files.