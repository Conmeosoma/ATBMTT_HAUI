# 📊 Tóm Tắt Dự Án - Project Overview

## 🎯 Mục Tiêu

Xây dựng một **ứng dụng Java hoàn chỉnh** triển khai **Chữ ký số Schnorr (Schnorr Digital Signature Algorithm)** với giao diện đồ họa (GUI) sử dụng **Java Swing**.

---

## 📦 Cấu Trúc Dự Án

### **Cấu trúc tệp tin:**

```
An Toàn Bảo Mật Thông Tin/
│
├── 📄 Tệp Java (Code chính)
│   ├── SchorrMain.java                 ⭐ ENTRY POINT - File chính để chạy
│   ├── SchorrSignatureParams.java      - Tham số hệ thống (p, q, g)
│   ├── SchorrKeyPair.java              - Cặp khóa (private, public)
│   ├── SchorrSignature.java            - Chữ ký (s, e)
│   ├── SchorrSignatureAlgorithm.java   - Triển khai thuật toán 3 giai đoạn
│   └── SchorrGUI.java                  - Giao diện Swing
│
├── 📋 Tài Liệu
│   ├── SCHNORR_README.md               - Tài liệu chi tiết (2000+ dòng)
│   ├── QUICKSTART.md                   - Hướng dẫn nhanh
│   └── PROJECT_SUMMARY.md              - File này
│
├── 🚀 Scripts Chạy
│   ├── run.bat                         - Script chạy trên Windows
│   └── run.sh                          - Script chạy trên Linux/Mac
│
└── 📁 Khác
    ├── .git/                           - Git repository
    ├── README.md                       - File readme gốc
    └── test.java                       - File test (nếu có)
```

---

## 🏗️ Kiến Trúc & Luồng Dữ Liệu

```
SchorrMain.java
    │
    ├─► Kiểm tra yêu cầu hệ thống
    │   ├─ Java version
    │   ├─ Bộ nhớ (RAM)
    │   ├─ Hệ điều hành
    │   ├─ GUI availability
    │   └─ Các lớp Java cần thiết
    │
    ├─► Cấu hình UI (Look & Feel)
    │
    └─► Khởi chạy SchorrGUI
            │
            ├─► SchorrSignatureAlgorithm (Thuật toán chính)
            │   │
            │   ├─ generateKeys()
            │   │  └─► SchorrSignatureParams (Tham số hệ thống)
            │   │  └─► SchorrKeyPair (Khóa sinh ra)
            │   │
            │   ├─ sign()
            │   │  └─► SchorrSignature (Chữ ký sinh ra)
            │   │
            │   └─ verify()
            │      └─► boolean (Kết quả xác minh)
            │
            └─► SchornGUI (Giao diện)
                ├─ Buttons: Generate Keys, Sign Message, Verify
                ├─ TextAreas: Display Private Key, Public Key, Signature
                └─ Labels: Verification Result (Green/Red)
```

---

## 📝 Mô Tả Chi Tiết Các File

### 1. **SchorrMain.java** ⭐ **[FILE CHÍNH]**

**Mục đích**: Entry point của ứng dụng

**Chức năng chính**:

- ✅ In thông báo chào mừng (Banner)
- ✅ Kiểm tra yêu cầu hệ thống:
  - Java version >= 8
  - Bộ nhớ (RAM) khả dụng
  - Hệ điều hành
  - GUI availability
  - Các lớp Java cần thiết
- ✅ Cấu hình Look & Feel Swing
- ✅ Khởi chạy SchorrGUI trên Event Dispatch Thread

**Xử lý lỗi**:

- Nếu có lỗi, in chi tiết và gợi ý khắc phục

**Dòng code**: ~300 dòng (có comment chi tiết)

---

### 2. **SchorrSignatureParams.java**

**Mục đích**: Lưu trữ tham số hệ thống của Schnorr

**Các thuộc tính**:

```java
private BigInteger p;  // Số nguyên tố lớn (512 bit)
private BigInteger q;  // Ước số nguyên tố (256 bit)
private BigInteger g;  // Phần tử sinh của nhóm con
```

**Phương thức chính**:

- `generateSystemParameters()` - Tạo tham số hệ thống ngẫu nhiên
  - Tạo q (256-bit safe prime)
  - Tạo p = 2q + 1 (Sophie Germain prime)
  - Tìm g sao cho g^q ≡ 1 (mod p)

**Dòng code**: ~70 dòng

---

### 3. **SchorrKeyPair.java**

**Mục đích**: Đại diện cặp khóa công khai/bí mật

**Các thuộc tính**:

```java
private BigInteger privateKey;     // Khóa bí mật x
private BigInteger publicKey;      // Khóa công khai y
private SchorrSignatureParams params;  // Tham số hệ thống
```

**Phương thức**:

- Getters: `getPrivateKey()`, `getPublicKey()`, `getParams()`
- `toString()` - Hiển thị rút gọn

**Dòng code**: ~50 dòng

---

### 4. **SchorrSignature.java**

**Mục đích**: Đại diện cho chữ ký Schnorr

**Các thuộc tính**:

```java
private BigInteger s;  // Thành phần s của chữ ký
private BigInteger e;  // Thành phần e (hash)
```

**Phương thức**:

- Getters: `getS()`, `getE()`
- `toString()` - Hiển thị hex format

**Dòng code**: ~50 dòng

---

### 5. **SchorrSignatureAlgorithm.java** 🔑 **[THUẬT TOÁN CHÍNH]**

**Mục đích**: Triển khai 3 giai đoạn của thuật toán Schnorr

**GIAI ĐOẠN 1: Phát sinh khóa (Key Generation)**

```java
public SchorrKeyPair generateKeys()
```

- Tạo tham số hệ thống (p, q, g)
- Chọn x ngẫu nhiên: x ∈ [1, q-1]
- Tính y = g^x mod p
- Return: SchorrKeyPair(x, y)

**GIAI ĐOẠN 2: Tạo chữ ký (Signing)**

```java
public SchorrSignature sign(String message) throws Exception
```

- Chọn k ngẫu nhiên: k ∈ [1, q-1]
- Tính r = g^k mod p
- Tính e = H(M || r) mod q (SHA-256)
- Tính s = (k + e·x) mod q
- Return: SchorrSignature(s, e)

**GIAI ĐOẠN 3: Xác minh chữ ký (Verification)**

```java
public boolean verify(String message, SchorrSignature signature) throws Exception
```

- Tính r' = g^s · y^(-e) mod p
- Tính e' = H(M || r') mod q
- Return: (e' == e)

**Hỗ trợ**:

- `hashSHA256()` - Tính hash SHA-256

**Dòng code**: ~280 dòng (có comment chi tiết)

---

### 6. **SchorrGUI.java** 🎨 **[GIAO DIỆN]**

**Mục đích**: Giao diện Swing cho ứng dụng

**Thành phần giao diện**:

| Thành phần              | Loại       | Chức năng                  |
| ----------------------- | ---------- | -------------------------- |
| Message Input           | JTextField | Nhập thông điệp cần ký     |
| Private Key Display     | JTextArea  | Hiển thị khóa bí mật       |
| Public Key Display      | JTextArea  | Hiển thị khóa công khai    |
| Signature Display       | JTextArea  | Hiển thị chữ ký (s, e)     |
| Verification Result     | JLabel     | Hiển thị kết quả (Xanh/Đỏ) |
| Generate Keys Button    | JButton    | Phát sinh khóa             |
| Sign Message Button     | JButton    | Ký thông điệp              |
| Verify Signature Button | JButton    | Xác minh chữ ký            |

**Luồng xử lý**:

1. `handleGenerateKeys()` - Gọi algorithm.generateKeys()
2. `handleSignMessage()` - Gọi algorithm.sign()
3. `handleVerifySignature()` - Gọi algorithm.verify(), hiển thị kết quả
   - Nếu hợp lệ: "✓ CHỮ KÝ HỢP LỆ" (nền xanh)
   - Nếu không hợp lệ: "✗ CHỮ KÝ KHÔNG HỢP LỆ" (nền đỏ)

**Màu sắc**:

- Primary (Steel Blue): #4682B4
- Success (Forest Green): #228B22
- Error (Crimson): #DC143C
- Background (Alice Blue): #F0F8FF

**Dòng code**: ~600 dòng (có comment chi tiết)

---

## 🔐 Công Thức Toán Học

### **Key Generation**

```
x ∈ᴿ [1, q-1]  (khóa bí mật)
y = g^x mod p  (khóa công khai)
```

### **Signing**

```
k ∈ᴿ [1, q-1]
r = g^k mod p
e = H(M || r) mod q
s = (k + e·x) mod q
σ = (s, e)
```

### **Verification**

```
r' = g^s · y^(-e) mod p
e' = H(M || r') mod q
Valid ⟺ e' = e
```

### **Tại sao nó hoạt động**

```
r' = g^s · y^(-e)
   = g^(k+ex) · (g^x)^(-e)
   = g^k · g^(ex) · g^(-ex)
   = g^k
   = r
```

---

## 🚀 Cách Chạy

### **Windows - Cách dễ nhất**

```bash
# Double-click file:
run.bat
```

### **Windows - Thủ công**

```cmd
javac SchorrSignatureParams.java SchorrKeyPair.java SchorrSignature.java SchorrSignatureAlgorithm.java SchorrGUI.java SchorrMain.java
java SchorrMain
```

### **Linux/Mac**

```bash
chmod +x run.sh
./run.sh
```

---

## 📊 Thống Kê Dự Án

| Chỉ số              | Giá trị         |
| ------------------- | --------------- |
| Tổng dòng code Java | ~1,250 dòng     |
| Số file Java        | 6 files         |
| Số lớp              | 6 classes       |
| Comment lines       | ~400 dòng       |
| Tài liệu Markdown   | ~3,000 dòng     |
| **Tổng cộng**       | **~4,250 dòng** |

### **Phân bố code**:

- `SchorrSignatureAlgorithm.java`: 28% (Thuật toán)
- `SchorrGUI.java`: 48% (Giao diện)
- `SchorrMain.java`: 24% (Entry point)

---

## ✨ Các Tính Năng

### **Bảo Mật**

- ✅ Sử dụng `java.math.BigInteger` cho số nguyên tố lớn
- ✅ `SecureRandom` cho phát sinh số ngẫu nhiên
- ✅ SHA-256 cho hàm băm
- ✅ Không lưu khóa bí mật ở bộ nhớ ngoài

### **Giao Diện**

- ✅ GUI chuyên nghiệp với Java Swing
- ✅ Hiển thị màu sắc (Xanh/Đỏ) cho kết quả
- ✅ Input validation
- ✅ Error handling với dialog boxes

### **Mã Nguồn**

- ✅ OOP hoàn toàn (6 classes độc lập)
- ✅ Comment chi tiết (tiếng Việt + tiếng Anh)
- ✅ Xử lý ngoại lệ toàn diện
- ✅ Clean code principles

### **Tài Liệu**

- ✅ SCHNORR_README.md (~3,000 dòng)
- ✅ QUICKSTART.md (~200 dòng)
- ✅ PROJECT_SUMMARY.md (File này)
- ✅ Comment trong source code

---

## 🧪 Kiểm Thử

### **Test Case 1: Chữ ký hợp lệ**

1. Click "Generate Keys"
2. Nhập: "Test message"
3. Click "Sign Message"
4. Click "Verify Signature"
5. Kết quả: ✓ CHỮ KÝ HỢP LỆ (Xanh)

### **Test Case 2: Chữ ký không hợp lệ (thay đổi thông điệp)**

1. Click "Generate Keys"
2. Nhập: "Original message"
3. Click "Sign Message"
4. Thay đổi thông điệp: "Modified message"
5. Click "Verify Signature"
6. Kết quả: ✗ CHỮ KÝ KHÔNG HỢP LỆ (Đỏ)

### **Test Case 3: Khóa khác**

1. Click "Generate Keys" (lần 1)
2. Nhập: "Test"
3. Click "Sign Message"
4. Click "Generate Keys" (lần 2 - khóa mới)
5. Click "Verify Signature"
6. Kết quả: ✗ CHỮ KÝ KHÔNG HỢP LỆ (Đỏ)

---

## 🎓 Khái Niệm Toán Học

### **Discrete Logarithm Problem (DLP)**

Cho biết y, g, p, tìm x sao cho:

```
y ≡ g^x (mod p)
```

Đây là bài toán **NP-hard** - không có thuật toán đa thức để giải nhanh.

### **Cryptographic Hash Function**

- **SHA-256**: 256-bit output
- **Tính chất**:
  - One-way (không thể reverse)
  - Collision-resistant (hai input khác không cùng output)
  - Deterministic (cùng input → cùng output)

---

## 📚 Tài Liệu Tham Khảo

- **Schnorr, C. P. (1989)**: "Efficient identification and signatures for smart cards"
- **RFC 8032**: Edwards-Curve Digital Signature Algorithm (EdDSA)
- **FIPS 186-4**: Digital Signature Standard (DSS)
- **Wikipedia**: Schnorr signature, Discrete logarithm

---

## ⚠️ Lưu Ý Bảo Mật

> ⚠️ **CẢNH BÁO**: Đây là ứng dụng **giáo dục**
>
> - Không sử dụng cho production
> - Số nguyên tố chỉ 256 bit (tiêu chuẩn là 2048 bit)
> - Không có bảo vệ timing side-channel

---

## 🎯 Yêu Cầu Ban Đầu

Ứng dụng này được tạo để đáp ứng các yêu cầu:

✅ **Thuật toán**: Sử dụng BigInteger cho các số nguyên tố lớn  
✅ **3 Giai đoạn**: Key Generation, Signing, Verification  
✅ **Hàm băm**: SHA-256  
✅ **Giao diện GUI**: Java Swing  
✅ **OOP**: Code hướng đối tượng  
✅ **Comment**: Giải thích chi tiết  
✅ **Màu sắc**: Hiển thị xanh/đỏ rõ ràng

---

## 📄 Giấy Phép & Tác Giả

- **Môn học**: An toàn Bảo mật Thông tin
- **Trường**: HAUI (Hanoi University of Industry)
- **Năm**: 2026
- **Ngôn ngữ**: Java 8+

---

## 🤝 Hỗ Trợ

Nếu gặp vấn đề:

1. Đọc `QUICKSTART.md`
2. Đọc `SCHNORR_README.md`
3. Kiểm tra yêu cầu hệ thống (xem lỗi từ SchorrMain)
4. Liên hệ giáo viên hướng dẫn

---

**Dự án hoàn chỉnh! 🎉**
