# Prototype Screenshots Directory — PA3-2026

Thư mục này dùng để lưu trữ toàn bộ ảnh chụp màn hình UI Prototype của các Use Case phục vụ nộp bài **Project Assignment 3 (PA3-2026)**.

---

## 📁 Cấu trúc thư mục

Mỗi Use Case có một thư mục riêng tương ứng với mã Use Case:

```text
prototypes/
├── UC-ACC-01/          # User Sign In & Authentication
│   ├── S01.png         # Basic Flow: Màn hình đăng nhập mặc định
│   ├── S02.png         # Basic Flow: Trạng thái đang đăng nhập (loading spinner)
│   ├── S03.png         # Basic Flow: Dashboard sau khi đăng nhập thành công
│   ├── S04.png         # AF-01: Lỗi sai thông tin đăng nhập
│   └── S05.png         # AF-02: Modal cảnh báo tài khoản bị khóa
│
├── UC-ACC-02/          # Manage User Profile & Credentials
│   ├── S01.png         # Basic Flow: Trang tổng quan hồ sơ
│   ├── S02.png         # Basic Flow: Form chỉnh sửa thông tin & đổi avatar
│   ├── S03.png         # Basic Flow: Trạng thái đang lưu hồ sơ
│   ├── S04.png         # AF-01/02: Modal đổi mật khẩu & báo lỗi
│   └── S05.png         # AF-03: Cảnh báo file ảnh quá 5MB
│
├── UC-ADM-01/          # Admin Portal Login & Role Authorization
│   ├── S01.png         # Basic Flow: Màn hình đăng nhập Web Admin
│   ├── S02.png         # Basic Flow: Đang xác thực
│   ├── S03.png         # Basic Flow: Dashboard quản trị tổng quan
│   ├── S04.png         # AF-01: Lỗi sai mật khẩu Admin
│   ├── S05.png         # AF-02: Lỗi chặn người dùng thường (403 Forbidden)
│   └── S06.png         # AF-03: Khóa đăng nhập do brute-force
│
├── UC-ADM-02/          # Search & Monitor User Account Registry
│   ├── S01.png         # Basic Flow: Bảng danh sách người dùng mặc định
│   ├── S02.png         # Basic Flow: Nhập ô tìm kiếm & lọc trạng thái
│   ├── S03.png         # Basic Flow: Kết quả danh sách đã lọc
│   ├── S04.png         # Basic Flow: Drawer trượt xem chi tiết telemetry
│   └── S05.png         # AF-01: Trạng thái tìm kiếm không có kết quả
│
├── UC-ADM-03/          # Lock and Unlock User Account
│   ├── S01.png         # Basic Flow: Modal xác nhận khóa tài khoản & nhập lý do
│   ├── S02.png         # Basic Flow: Đang xử lý khóa
│   ├── S03.png         # Basic Flow: Bảng user hiển thị huy hiệu LOCKED màu đỏ
│   ├── S04.png         # AF-01: Cảnh báo bắt buộc nhập lý do >= 10 ký tự
│   ├── S05.png         # AF-03: Cảnh báo Admin không thể tự khóa chính mình
│   └── S06.png         # AF-04: Modal mở khóa tài khoản (Unlock)
│
└── UC-ADM-04/          # Configure Global System Taxonomies & Defaults
    ├── S01.png         # Basic Flow: Bảng danh mục & cấu hình tiền tệ
    ├── S02.png         # Basic Flow: Modal thêm danh mục mới
    ├── S03.png         # Basic Flow: Đang lưu danh mục
    ├── S04.png         # Basic Flow: Danh mục mới được thêm vào danh sách
    ├── S05.png         # AF-01: Lỗi trùng tên danh mục
    └── S06.png         # AF-02: Cảnh báo không được xóa danh mục đang có giao dịch
```

---

## 📌 Quy chuẩn định dạng ảnh:
1. **Định dạng file:** Luôn lưu dưới dạng `.png` hoặc `.jpg` (khuyến nghị `.png` để hình ảnh sắc nét).
2. **Quy tắc đặt tên:** `S<số thứ tự 2 chữ số>.png` (Ví dụ: `S01.png`, `S02.png`...).
3. **Kích thước:** Cắt gọn khung màn hình, tỷ lệ rõ ràng, không chụp thừa thanh taskbar hay các tab trình duyệt ngoài lề.

