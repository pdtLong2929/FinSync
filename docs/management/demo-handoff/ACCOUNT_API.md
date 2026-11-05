# API tài khoản — Bàn giao demo PA3
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ xác nhận*

Backend đã triển khai đăng ký, đăng nhập, refresh token, đăng xuất, xem và cập nhật hồ sơ. Phần này chưa thay đổi Android. Hòa phụ trách Spec Kit, Nhật phụ trách Backend, Đạt phụ trách Android và Khiêm phụ trách Testing.

Tài liệu nội bộ bằng tiếng Việt. Tên field, endpoint, mã lỗi, lệnh chạy và thông báo trong response mẫu giữ đúng hợp đồng API.

## 1. Cách chạy
> *Thực hiện: Nhật | Rà soát: Chờ xác nhận*

Cần Java 21, Maven, PostgreSQL 16 và Redis. Đọc [hướng dẫn database](ACCOUNT_PERSISTENCE.md) trước khi dùng database hiện có; không xóa dữ liệu hoặc tự bật baseline để né lỗi migration.

Nếu dùng Docker Compose, tạo file `.env` không đưa vào Git tại thư mục gốc với `DB_PASSWORD` và `JWT_SECRET` ngẫu nhiên dài ít nhất 32 byte UTF-8. Sinh khóa bằng PowerShell:

```powershell
[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

Chạy từ thư mục gốc:

```powershell
docker compose up --build -d postgres redis backend
```

Redis dùng AOF để lưu bền, volume riêng và chính sách không tự loại key khi thiếu bộ nhớ; Compose không công khai cổng Redis ra host. Dùng HTTPS và giới hạn truy cập mạng khi triển khai từ xa.

Nếu chạy Java ngoài Docker, đưa các biến trong [.env.example](.env.example) vào môi trường của tiến trình, gồm `DB_PASSWORD`, `JWT_SECRET` và địa chỉ PostgreSQL/Redis thật. Spring Boot không tự đọc `.env`. Chạy `mvn spring-boot:run` từ `src/backend`. Lần kiểm chứng trước dùng Maven cài sẵn vì Windows Maven Wrapper lỗi khởi động trong môi trường kiểm tra.

JWT mặc định hết hạn sau 900 giây; `JWT_EXPIRATION_MS` cho phép 1.000–900.000 mili giây. Không commit thông tin đăng nhập hoặc token thật.

## 2. Hợp đồng để nối Android
> *Thực hiện: Nhật | Phối hợp: Đạt | Rà soát: Chờ xác nhận*

Emulator Android chuẩn và BE cùng máy: `http://10.0.2.2:8080/api/v1/`. Nếu gọi BE trên máy Nhật: `http://<IP-LAN-máy-Nhật>:8080/api/v1/`, cùng mạng và cổng truy cập được. Không hardcode IP cá nhân.

HTTP chỉ dùng debug, bản phát hành dùng HTTPS. JSON dùng camelCase. API bảo vệ cần `Authorization: Bearer <accessToken>`. Không dùng cookie trình duyệt.

| Method / đường dẫn | Dữ liệu gửi | Thành công |
|---|---|---|
| POST `auth/register` | `displayName`, `email`, `password` | 201: `id`, `email`, `displayName`; chuyển về login. |
| POST `auth/login` | `email`, `password`; tùy chọn `rememberMe`, `deviceLabel` | 200: cặp token. |
| POST `auth/refresh` | `refreshToken` | 200: cặp token mới; không cần access token. |
| POST `auth/logout` | Không body; Bearer token | 204, không body: phiên hiện tại bị thu hồi. |
| GET `users/me` | Bearer token | 200: hồ sơ. |
| PATCH `users/me` | Bearer token; `version` và ít nhất một trong `displayName`, `phoneNumber` | 200: hồ sơ sau cập nhật. |

Ví dụ cặp token; thời gian chỉ minh họa:

```json
{"tokenType":"Bearer","accessToken":"<jwt>","expiresIn":900,"refreshToken":"<opaque-token>","refreshExpiresAt":"2026-11-12T10:00:00Z"}
```

Ví dụ hồ sơ:

```json
{"id":"1733f7f5-5f28-4532-9e83-683c62e033ec","email":"demo@example.com","displayName":"Demo User","phoneNumber":null,"version":0}
```

- Email được trim, chuyển chữ thường và phải duy nhất. Tên hiển thị được trim, dài 1–100 ký tự.
- Mật khẩu đăng ký dài ít nhất 8 ký tự theo phép đếm chuỗi Java và tối đa **72 byte UTF-8** để dùng BCrypt, không trim/cắt ngầm. Android cần thay giới hạn cũ 128 ký tự và bổ sung tên hiển thị. Xác nhận mật khẩu chỉ xử lý phía client.
- Số điện thoại tùy chọn, tối đa 30 ký tự, gồm chữ số/khoảng trắng/`+`/`-`/ngoặc và ít nhất một chữ số. PATCH null/rỗng xóa số; bỏ qua field giữ nguyên. Tên không được null/rỗng.
- Field không được hỗ trợ bị từ chối. Không gửi role, status, email hoặc userId trong PATCH.
- PATCH cần version mới nhất. Nếu giá trị không thay đổi, version có thể giữ nguyên.
- Chủ hồ sơ lấy từ JWT; response không lộ hash mật khẩu/refresh token.

Ví dụ cập nhật: `{"version":0,"displayName":"New Name","phoneNumber":null}`.

## 3. Lỗi và quản lý phiên
> *Thực hiện: Nhật | Phối hợp: Đạt, Khiêm | Rà soát: Chờ xác nhận*

Cấu trúc lỗi:

```json
{"code":"VALIDATION_ERROR","message":"Invalid request","fieldErrors":{"displayName":"Use 1 to 100 characters"},"traceId":"..."}
```

`fieldErrors` là `{}` nếu lỗi không gắn field. FE dựa trên `code`, không phân tích chuỗi message.

| HTTP / mã lỗi | Cách xử lý phía client |
|---|---|
| 400 VALIDATION_ERROR | Giữ bản nháp và hiển thị lỗi dữ liệu. |
| 409 EMAIL_ALREADY_EXISTS | Chọn email khác hoặc về đăng nhập. |
| 401 INVALID_CREDENTIALS | Báo chung email/mật khẩu không hợp lệ. |
| 401 UNAUTHORIZED | Refresh một lần nếu phù hợp; nếu không thì login. |
| 401 INVALID_REFRESH_TOKEN | Xóa phiên cục bộ không hợp lệ; yêu cầu login. |
| 403 ACCOUNT_LOCKED | Dừng thao tác cần xác thực. |
| 409 PROFILE_VERSION_CONFLICT | Tải lại, yêu cầu người dùng xem trước khi lưu. |
| 503 AUTH_SERVICE_UNAVAILABLE | Báo lỗi tạm thời; không coi là sai thông tin đăng nhập. |

Access token HS256 được kiểm tra chữ ký, issuer, audience, thời hạn và trạng thái user/phiên. Mọi request bảo vệ kiểm tra blacklist Redis; Redis lỗi thì chặn với 503.

Refresh token có hạn tuyệt đối 7 ngày, hoặc 30 ngày với rememberMe. Mỗi lần dùng thay token cũ, không kéo dài hạn phiên. Android chỉ cho một lần refresh chạy đồng thời và thay token trong kho an toàn một cách nhất quán. Request gốc retry tối đa một lần; không refresh đệ quy. Mất response sau khi server đã xoay token có thể buộc login lại.

Refresh không tự vô hiệu access JWT cũ. Logout commit thu hồi phiên trong database trước, sau đó ghi blacklist JWT. Nếu Redis ghi lỗi, response là 503 nhưng phiên vẫn bị thu hồi; thử lại có thể nhận 401. Các phiên khác vẫn hoạt động. Nếu mất dữ liệu blacklist Redis, thu hồi các phiên hiện có trước khi mở lại xác thực.

Quên mật khẩu, endpoint khóa user và API admin ngoài phạm vi demo.

## 4. Kiểm thử và bàn giao
> *Thực hiện: Nhật | Kiểm tra độc lập tiếp theo: Khiêm | Rà soát: Chờ xác nhận*

Chạy `mvn test` trên PostgreSQL/Redis test riêng với biến DB/Redis và JWT secret dành cho test. Test HTTP commit tài khoản/phiên thật; không dùng database production. Test Redis lỗi mô phỏng lỗi ở lớp client; test blacklist thông thường dùng Redis thật.

Kết quả kiểm chứng trước ngày **05/11/2026: 13 test đạt** — 8 HTTP API, 4 persistence, 1 khởi tạo ứng dụng trên PostgreSQL 16/Redis 7 tạm. Bao phủ đăng ký/refresh đồng thời, băm dữ liệu, quyền/version/null hồ sơ, tài khoản khóa, phiên hết hạn, JWT sai chữ ký/issuer/audience/hết hạn, blacklist và việc thu hồi phiên vẫn giữ khi Redis ghi lỗi. Chưa khẳng định Android end-to-end đã chạy.

Đạt nối 6 API; Khiêm kiểm tra độc lập API rồi Android; Hòa đồng bộ Spec Kit. Nhật hỗ trợ kết nối và sửa lỗi BE.

Nguồn kỹ thuật: [quản lý phiên stateless trong Spring Security](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/session-management.html) và [JWT HMAC với Nimbus](https://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-hmac).
