# Ứng dụng Mô phỏng Chữ ký Số Schnorr (Schnorr Digital Signature Simulator)

## 📋 Giới thiệu

Đây là một ứng dụng Java hoàn chỉnh triển khai **Thuật toán Chữ ký số Schnorr** (Schnorr Digital Signature Algorithm) với giao diện đồ họa (GUI) sử dụng **Java Swing**.

Schnorr Digital Signature là một sơ đồ chữ ký số dựa trên **Bài toán Logarit Rời Rạc (Discrete Logarithm Problem - DLP)**, được phát triển bởi Claus Schnorr. Nó nổi tiếng vì:

- ✅ **Hiệu quả cao**: Tính toán nhanh, chữ ký nhỏ
- ✅ **Bảo mật mạnh**: Dựa trên DLP, khó phá vỡ
- ✅ **Đơn giản**: Dễ hiểu và triển khai so với RSA/DSA

## 🏗️ Cấu trúc kiến trúc

### 3 Giai đoạn chính:

### **GIAI ĐOẠN 1: Phát sinh khóa (Key Generation)**

```
Đầu vào: Tham số hệ thống (p, q, g)
Quá trình:
  1. Chọn khóa bí mật x ngẫu nhiên từ [1, q-1]
  2. Tính khóa công khai y = g^x mod p
Đầu ra: Cặp khóa (x, y)
```

**Công thức:**

- Khóa bí mật: $x \in_R [1, q-1]$
- Khóa công khai: $y = g^x \bmod p$

---

### **GIAI ĐOẠN 2: Tạo chữ ký (Signing)**

```
Đầu vào: Thông điệp M, Khóa bí mật x
Quá trình:
  1. Chọn số ngẫu nhiên k từ [1, q-1]
  2. Tính r = g^k mod p
  3. Tính e = H(M || r) sử dụng SHA-256
  4. Tính s = k + e*x mod q
Đầu ra: Chữ ký (s, e)
```

**Công thức:**

- Chọn: $k \in_R [1, q-1]$
- Tính: $r = g^k \bmod p$
- Hash: $e = H(M || r) \bmod q$
- Chữ ký: $s = (k + e \cdot x) \bmod q$

---

### **GIAI ĐOẠN 3: Xác minh chữ ký (Verification)**

```
Đầu vào: Thông điệp M, Chữ ký (s, e), Khóa công khai y
Quá trình:
  1. Tính r' = g^s * y^(-e) mod p
  2. Tính e' = H(M || r')
  3. Kiểm tra: nếu e' == e thì chữ ký hợp lệ
Đầu ra: TRUE (hợp lệ) hoặc FALSE (không hợp lệ)
```

**Công thức:**

- Tính: $r' = g^s \cdot y^{-e} \bmod p$
- Hash: $e' = H(M || r') \bmod q$
- Kiểm tra: $e' \stackrel{?}{=} e$

**Lý thuyết (tại sao nó hoạt động):**
$$r' = g^s \cdot y^{-e} = g^{k+ex} \cdot (g^x)^{-e} = g^k = r$$

Do đó, nếu chữ ký hợp lệ thì $e' = H(M || r') = H(M || r) = e$ ✓

---

## 📁 Cấu trúc tệp

```
SchorrSignatureParams.java    - Tham số hệ thống (p, q, g)
SchorrKeyPair.java            - Cặp khóa công khai/bí mật
SchorrSignature.java          - Chữ ký (s, e)
SchorrSignatureAlgorithm.java - Triển khai thuật toán
SchorrGUI.java                - Giao diện Swing
README.md                      - Hướng dẫn này
```

---

## 🔧 Cài đặt và chạy

### **Yêu cầu:**

- Java Development Kit (JDK) 8 hoặc mới hơn
- Không cần thư viện bên ngoài (sử dụng java.math.BigInteger, javax.swing)

### **Biên dịch:**

```bash
cd "An Toàn Bảo Mật Thông Tin"
javac SchorrSignatureParams.java
javac SchorrKeyPair.java
javac SchorrSignature.java
javac SchorrSignatureAlgorithm.java
javac SchorrGUI.java
```

### **Chạy:**

```bash
java SchorrGUI
```

---

## 💻 Hướng dẫn sử dụng giao diện

### **Bước 1: Phát sinh khóa**

1. Nhấp nút **"Generate Keys"**
2. Chương trình sẽ:
   - Phát sinh tham số hệ thống (p, q, g)
   - Phát sinh khóa bí mật x
   - Tính khóa công khai y = g^x mod p
3. Kết quả hiển thị ở hai khung text "KHÓA BÍ MẬT" và "KHÓA CÔNG KHAI"

### **Bước 2: Nhập thông điệp và ký**

1. Nhập thông điệp vào ô **"Thông điệp cần ký"**
2. Nhấp nút **"Sign Message"**
3. Chương trình sẽ:
   - Chọn k ngẫu nhiên
   - Tính r = g^k mod p
   - Tính e = H(M || r)
   - Tính s = (k + e\*x) mod q
4. Chữ ký (s, e) hiển thị ở khung "CHỮ KÝ"

### **Bước 3: Xác minh chữ ký**

1. Nhấp nút **"Verify Signature"**
2. Chương trình sẽ:
   - Tính r' = g^s \* y^(-e) mod p
   - Tính e' = H(M || r')
   - So sánh e' với e
3. Kết quả hiển thị ở dưới:
   - ✓ **CHỮ KÝ HỢP LỆ** (nền xanh) - nếu e' = e
   - ✗ **CHỮ KÝ KHÔNG HỢP LỆ** (nền đỏ) - nếu e' ≠ e

### **Thử nghiệm tính chất:**

- Nếu bạn thay đổi thông điệp sau khi ký, chữ ký sẽ trở thành **không hợp lệ**
- Chỉ khóa công khai tương ứng với khóa bí mật đã ký mới có thể xác minh chữ ký

---

## 🔐 Các đặc điểm bảo mật

### **1. Sử dụng BigInteger cho các số nguyên tố lớn**

```java
BigInteger q = BigInteger.probablePrime(256, random);
BigInteger p = q.multiply(BigInteger.TWO).add(BigInteger.ONE);
```

- **p**: Số nguyên tố an toàn (Safe Prime) có ~512 bit
- **q**: Số nguyên tố lớn có 256 bit
- Kích thước này đảm bảo bảo mật vừa phải cho mục đích demo

### **2. Hàm băm SHA-256**

```java
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] hashBytes = digest.digest(input.getBytes());
BigInteger hashValue = new BigInteger(1, hashBytes);
```

- Sử dụng SHA-256 tiêu chuẩn (FIPS 180-4)
- Hash output (256 bits) được lấy modulo q để vừa với phạm vi

### **3. Số ngẫu nhiên an toàn**

```java
SecureRandom random = new SecureRandom();
BigInteger k = new BigInteger(q.bitLength(), random);
```

- Sử dụng `SecureRandom` thay vì `Random` thông thường
- Đảm bảo k không bao giờ bị lặp lại

### **4. Không có thông tin rò rỉ**

- Chữ ký (s, e) không bao gồm thông tin về khóa bí mật
- Khóa bí mật x không bao giờ được tiết lộ

---

## 📊 Ví dụ toán học

### **Thí dụ cụ thể (với số nhỏ để minh họa):**

**Tham số hệ thống (được tạo ngẫu nhiên):**

- p = 2357 (số nguyên tố)
- q = 1178 (ước số nguyên tố của p-1)
- g = 1234 (phần tử sinh)

**GIAI ĐOẠN 1: Phát sinh khóa**

- Chọn x = 765 (khóa bí mật)
- Tính y = 1234^765 mod 2357 = 1845 (khóa công khai)

**GIAI ĐOẠN 2: Ký thông điệp M = "Hello"**

- Chọn k = 432
- Tính r = 1234^432 mod 2357 = 456
- Tính e = SHA256("Hello456") mod 1178 = 234
- Tính s = (432 + 234\*765) mod 1178 = 567

**Chữ ký: (s=567, e=234)**

**GIAI ĐOẠN 3: Xác minh**

- Tính r' = 1234^567 \* 1845^(-234) mod 2357 = 456
- Tính e' = SHA256("Hello456") mod 1178 = 234
- Kiểm tra: e' == e? → 234 == 234 → ✓ HỢP LỆ

---

## 🎓 Các khái niệm toán học

### **Logarit rời rạc (Discrete Logarithm)**

Cho một ngôn ngữ $g$, số modulo $p$, và giá trị $y$, tìm $x$ sao cho:
$$y \equiv g^x \pmod{p}$$

Đây là bài toán khó - không có thuật toán cổ điển nào có thể giải nó trong thời gian đa thức để giải $x$ từ $y$.

### **Tính chất sử dụng**

- **Tính bí mật**: Từ $y = g^x \bmod p$, không thể suy ra $x$
- **Không phủ nhận (Non-repudiation)**: Chỉ người biết $x$ mới có thể tạo chữ ký hợp lệ

### **Độ phức tạp tính toán**

- **Key Generation**: O(log q) phép nhân modulo
- **Signing**: O(log q) phép nhân modulo
- **Verification**: O(log q) phép nhân modulo

---

## 🧪 Kiểm thử ứng dụng

### **Test Case 1: Chữ ký hợp lệ**

```
1. Generate Keys
2. Nhập: "HAUI An toan bao mat thong tin"
3. Sign Message → Chữ ký (s, e)
4. Verify Signature → ✓ CHỮ KÝ HỢP LỆ (Xanh)
```

### **Test Case 2: Chữ ký không hợp lệ (thay đổi thông điệp)**

```
1. Generate Keys
2. Nhập: "HAUI An toan bao mat thong tin"
3. Sign Message → Chữ ký (s, e)
4. Thay đổi thông điệp: "HAUI An toan bao mat..."
5. Verify Signature → ✗ CHỮ KÝ KHÔNG HỢP LỆ (Đỏ)
```

### **Test Case 3: Phát sinh khóa mới**

```
1. Generate Keys (lần 1)
2. Nhập: "Test message"
3. Sign Message → Chữ ký 1
4. Generate Keys (lần 2 - khóa khác)
5. Verify Signature (với chữ ký 1) → ✗ KHÔNG HỢP LỆ
```

---

## 🎨 Giao diện (UI)

### **Thành phần:**

1. **Tiêu đề**: "SCHNORR DIGITAL SIGNATURE ALGORITHM SIMULATOR"
2. **Vùng nhập**: Ô nhập thông điệp
3. **Ba nút bấm**:
   - 🔵 **Generate Keys** (Màu xanh dương)
   - 🟢 **Sign Message** (Màu xanh lá)
   - 🔴 **Verify Signature** (Màu đỏ)
4. **Hiển thị kết quả**:
   - Khóa bí mật (Private Key)
   - Khóa công khai (Public Key)
   - Chữ ký (Signature)
5. **Xác minh**: Hiển thị "✓ CHỮ KÝ HỢP LỆ" (xanh) hoặc "✗ CHỮ KÝ KHÔNG HỢP LỆ" (đỏ)

### **Tùy chọn giao diện:**

- Font: Arial (tiêu đề), Courier New (nội dung)
- Bố cục: BorderLayout + GridLayout
- Màu sắc: Steel Blue (#4682B4), Forest Green (#228B22), Crimson (#DC143C)

---

## 📝 Comment mã nguồn

Tất cả các phương thức đều có comment chi tiết giải thích:

- **Giai đoạn thuật toán**: Comment mô tả từng bước
- **Công thức toán học**: Công thức được viết rõ ràng
- **Biến**: Giải thích ý nghĩa của từng biến

Ví dụ:

```java
/**
 * GIAI ĐOẠN 1: Phát sinh khóa (Key Generation)
 *
 * Quá trình:
 * 1. Nhận các tham số hệ thống (p, q, g)
 * 2. Chọn khóa bí mật x ngẫu nhiên từ [1, q-1]
 * 3. Tính khóa công khai y = g^x mod p
 */
public SchorrKeyPair generateKeys() { ... }
```

---

## 🚀 Mở rộng ứng dụng

Các hướng mở rộng có thể:

1. **Tăng độ an toàn**: Sử dụng số nguyên tố lớn hơn (1024-2048 bit)
2. **Lưu/tải khóa**: Thêm chức năng serialize cặp khóa
3. **Đa người dùng**: Quản lý nhiều cặp khóa
4. **Chứng chỉ**: Liên kết khóa công khai với danh tính
5. **Batch operations**: Ký/xác minh nhiều thông điệp
6. **Hiệu suất**: Tối ưu hóa sử dụng fast modular exponentiation

---

## 📚 Tài liệu tham khảo

- **Schnorr, C. P. (1989)**: "Efficient identification and signatures for smart cards"
- **FIPS 186-4**: Digital Signature Standard (DSS)
- **RFC 8032**: Edwards-Curve Digital Signature Algorithm (EdDSA)
- **Wikipedia**: Schnorr signature

---

## ⚠️ Lưu ý bảo mật

> ⚠️ **CẢNH BÁO**: Đây là **ứng dụng giáo dục** dùng để học tập.
>
> - Không sử dụng cho mục đích sản xuất
> - Không dùng cho bảo vệ dữ liệu thật
> - Số nguyên tố được dùng quá nhỏ (256 bit) so với tiêu chuẩn (2048 bit)
> - Không có bảo vệ chống tấn công timing side-channel

---

## 📄 Giấy phép

Mã nguồn này được cung cấp cho mục đích giáo dục tại HAUI (Hanoi University of Industry).

---

## ✏️ Tác giả & Ngày

- **Môn học**: An toàn Bảo mật Thông tin
- **Trường**: HAUI (Hanoi University of Industry)
- **Thời gian**: 2026
- **Ngôn ngữ**: Tiếng Việt + Tiếng Anh

---

## 🤝 Liên hệ & Hỗ trợ

Nếu có câu hỏi hoặc vấn đề, vui lòng liên hệ giáo viên hướng dẫn.

---

**Chúc bạn học tập vui vẻ! 🎓**
