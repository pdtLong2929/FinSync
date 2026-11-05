# Database và repository cho tính năng tài khoản
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ xác nhận*

Tài liệu nội bộ bằng tiếng Việt, dùng cùng [hợp đồng API](ACCOUNT_API.md).

## 1. Migration và chuẩn bị database
> *Thực hiện: Nhật | Rà soát: Chờ xác nhận*

Flyway quản lý schema; Hibernate chỉ kiểm tra mapping entity qua `ddl-auto=validate`. Migration V1 tạo `users`, `auth_sessions`, ràng buộc và index; không tạo sẵn tài khoản.

Dùng PostgreSQL phát triển trống, cấu hình `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` trước khi chạy. Spring Boot không tự đọc file môi trường. Kiểm tra tên database: Compose và `.env.example` dùng `FinSync`, còn URL mặc định trong `application.properties` tại thời điểm rà soát dùng `finsync`. Đặt DB_URL theo database thực tế, không coi hai tên là một.

Không bật tự động baseline để né lỗi schema đã tồn tại: baseline version 1 có thể khiến Flyway bỏ qua migration này. Kiểm tra và đối chiếu schema từng được Hibernate tạo trước khi chuyển sang Flyway; không xóa dữ liệu hiện có để chạy migration cho thuận tiện.

Callback entity Java quản lý thời gian tạo/sửa. Cập nhật trực tiếp bằng SQL phải duy trì `updated_at` và `version` phù hợp.

## 2. Vai trò repository và service
> *Thực hiện: Nhật | Rà soát: Chờ xác nhận*

Repository cung cấp truy vấn; tìm thấy dữ liệu không đồng nghĩa đã xác thực request. Service/Security thực hiện:

- Chuẩn hóa email trước truy vấn, xử lý đăng ký đồng thời bằng ràng buộc UNIQUE.
- Kiểm tra trạng thái user, hạn phiên, trạng thái thu hồi và blacklist.
- Chạy truy vấn có khóa trong transaction; khóa user trước session khi cần cả hai.
- Sau khi khóa, kiểm tra lại hash refresh rồi xoay token trong cùng transaction.
- Thu hồi phiên qua entity được quản lý để version/timestamp được cập nhật.

Hai repository của demo là `UserRepository`, `AuthSessionRepository`. Blacklist nằm trong Redis, không có bảng blacklist PostgreSQL.

## 3. Kiểm thử persistence
> *Thực hiện: Nhật | Kiểm tra độc lập tiếp theo: Khiêm | Rà soát: Chờ xác nhận*

`AccountPersistenceTests` cần **database test riêng**, cung cấp qua DB_URL, DB_USERNAME, DB_PASSWORD và JWT_SECRET dành cho test dài ít nhất 32 byte UTF-8 để khởi tạo ứng dụng.

Chạy từ `src/backend`:

```powershell
mvn -Dtest=AccountPersistenceTests test
```

Dữ liệu của test persistence được rollback nhưng schema Flyway vẫn được giữ. Không dùng production. Phân biệt với `AccountApiTests`: request HTTP chạy transaction riêng và commit dữ liệu thật, nên cũng cần database tạm riêng. Xem [hướng dẫn API](ACCOUNT_API.md) để chạy toàn bộ test với Redis.
