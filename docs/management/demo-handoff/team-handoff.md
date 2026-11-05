# Bàn giao chung demo PA3 — Nhật, Hòa, Đạt và Khiêm

## 1. Phạm vi demo

Nhóm chức năng **quản lý tài khoản**: đăng ký → đăng nhập → xem/sửa tên và số điện thoại → refresh token → đăng xuất. Email chỉ đọc sau đăng ký.

Mục tiêu: chạy thật xuyên suốt **Android → Backend → PostgreSQL**, có Redis phục vụ blacklist. Chưa triển khai quên/đổi mật khẩu, đổi email, upload avatar, admin web hoặc nghiệp vụ tài chính trong đợt demo này.

## 2. Nhật đã hoàn thành những gì?

| Phần việc | Kết quả đã có |
|---|---|
| Entity và enum | `BaseEntity`, `User`, `AuthSession`; UUID, timestamp UTC, version, quan hệ và các enum tài khoản/phiên. |
| Database | Flyway tạo `users`, `auth_sessions`, FK, index và ràng buộc. Hibernate kiểm tra mapping, không tự sửa schema. |
| Repository | Truy vấn tài khoản/phiên, truy vấn có khóa phục vụ cập nhật và refresh đồng thời. |
| Đăng ký | Validation, chuẩn hóa email, chặn email trùng, băm mật khẩu BCrypt. |
| Xác thực | Login, JWT access token, refresh token xoay vòng, thu hồi phiên khi logout, blacklist Redis. |
| Hồ sơ | Xem/sửa đúng user từ token; không cho sửa quyền hoặc tài khoản khác; kiểm tra version tránh ghi đè. |
| API và lỗi | Controller, DTO, service, định dạng lỗi chung và 6 endpoint để Android sử dụng. |
| Cấu hình chạy | Cấu hình PostgreSQL/Redis, Compose, biến môi trường, hướng dẫn chạy. Nhật đã báo tự chạy/cấu hình môi trường demo; địa chỉ thực tế cần bàn giao cho Đạt/Khiêm. |
| Kiểm thử BE | Lần kiểm chứng trước có **13 test đạt**: 8 HTTP API, 4 persistence, 1 khởi tạo ứng dụng trên PostgreSQL 16/Redis 7 tạm riêng. |

Các API, cùng tiền tố `/api/v1`:

| API | Chức năng |
|---|---|
| `POST /auth/register` | Tạo tài khoản; thành công chuyển về đăng nhập. |
| `POST /auth/login` | Cấp access token và refresh token. |
| `POST /auth/refresh` | Cấp cặp token mới, thay refresh token cũ. |
| `POST /auth/logout` | Thu hồi phiên hiện tại. |
| `GET /users/me` | Lấy hồ sơ đang đăng nhập. |
| `PATCH /users/me` | Sửa tên/số điện thoại, kiểm tra version. |

**Trạng thái bàn giao:** BE đã được kiểm thử riêng; chưa coi Android đã nối xong, Khiêm đã nghiệm thu hoặc Spec Kit đã hoàn thiện. Nhật tiếp tục hỗ trợ kết nối, sửa lỗi BE, bàn giao commit/phiên bản và giữ server hoạt động theo lịch thống nhất.

## 3. Việc tiếp theo của từng người

| Người | Cần làm | Đầu ra |
|---|---|---|
| **Hòa — Spec Kit** | Kiểm tra cấu hình hiện có; hoàn thiện spec/plan/tasks; ghi nhận BE đã có; đưa việc tích hợp, kiểm thử và sửa lỗi vào quy trình Spec Kit. Đối chiếu yêu cầu với Nhật/Đạt/Khiêm. | Một bộ artifacts nhất quán, task/trạng thái đúng thực tế, tài liệu và bằng chứng cần nộp. Không mô tả ngược rằng Spec Kit đã dẫn dắt phần việc trước đó nếu chưa diễn ra. |
| **Đạt — FE Android** | Nối 6 API; thêm tên ở đăng ký, màn hình sửa hồ sơ; quản lý token an toàn; xử lý refresh, version cũ, loading, lỗi và logout. | Luồng tài khoản dùng API thật, commit/nhánh hoặc APK, hướng dẫn cấu hình URL và các lỗi còn biết. |
| **Khiêm — Testing** | Lập matrix từ spec/API; kiểm tra API độc lập, sau đó Android end-to-end; báo lỗi, retest và chạy thử kịch bản demo. | Kết quả expected/actual, môi trường/phiên bản, bằng chứng và kết luận có lỗi chặn demo hay không. Không lấy 13 test của Nhật thay cho nghiệm thu độc lập. |

## 4. Thứ tự phối hợp từ bây giờ

1. **Nhật bàn giao kết nối:** gửi nhánh/commit cần dùng, URL thực tế, thời gian server hoạt động và tài liệu API. FE không cần JWT secret hoặc mật khẩu database.
2. **Hòa đồng bộ Spec Kit; Đạt/Khiêm kiểm tra kết nối:** làm song song. Nếu spec và contract khác nhau, trao đổi trước khi đổi code.
3. **Đạt nối lần lượt:** đăng ký → login + xem hồ sơ → cập nhật hồ sơ → refresh → logout. Khiêm có thể kiểm tra API trước khi Android hoàn thiện.
4. **Khiêm kiểm tra Android:** đăng ký, login, sửa hồ sơ, mở lại vẫn thấy dữ liệu, logout và kiểm tra phiên cũ bị chặn; chạy thêm các nhánh lỗi đã thống nhất.
5. **Nhật/Đạt sửa lỗi, Khiêm retest, Hòa cập nhật artifacts.** Phân biệt lỗi UI, API, cấu hình mạng và yêu cầu chưa rõ.
6. **Cả nhóm chốt demo:** phân công người quay/thuyết minh/upload; chuẩn bị source, hướng dẫn chạy, Spec Kit artifacts và link YouTube theo đề.

Địa chỉ kết nối:
- Emulator Android chuẩn và BE cùng máy: `http://10.0.2.2:8080/api/v1/`.

- Làm việc trên branch task/demo_user_management

## 5. Quy tắc quan trọng khi tích hợp

- Đăng ký thành công chưa tạo phiên; cần login. Mật khẩu tối đa 72 byte UTF-8 theo BE, không giữ giới hạn cũ 128 ký tự của FE.
- PATCH gửi version mới nhất và trường hồ sơ cần sửa; không gửi userId/role/status/email.
- Refresh token cũ mất hiệu lực sau khi xoay; Android chỉ chạy một lần refresh đồng thời. Refresh không tự blacklist access token cũ.
- Logout thu hồi phiên; Redis lỗi sau commit không phục hồi phiên. Lỗi mạng/503 không được báo thành công hoặc sai mật khẩu.
- Báo lỗi cần bước tái hiện, HTTP status, mã lỗi và traceId khi có. Che token/secret trong ảnh và log.
- Test gây lỗi dịch vụ, sửa trạng thái DB hoặc đổi TTL dùng môi trường riêng và phối hợp Nhật.

## 6. Tài liệu gửi kèm

| Tài liệu tiếng Việt | Ai đọc và dùng làm gì |
|---|---|
| [DOCKER_DEMO.md](DOCKER_DEMO.md) | Cả ba: chạy BE/PostgreSQL/Redis bằng một lệnh, kiểm tra health và cấu hình LAN. |
| [ACCOUNT_API.md](ACCOUNT_API.md) | Cả ba: cách chạy, endpoint, request/response, validation, mã lỗi và xử lý token. |
| [ACCOUNT_PERSISTENCE.md](ACCOUNT_PERSISTENCE.md) | Hòa/Khiêm và người chạy BE: migration, repository, database và điều kiện chạy test. |

## 7. Điều kiện hoàn thàn

- [ ] BE truy cập được từ môi trường của Đạt/Khiêm; Nhật xử lý lỗi BE phát sinh.
- [ ] Đạt hoàn thiện luồng tài khoản thật, dữ liệu lưu bền và phiên hoạt động đúng.
- [ ] Khiêm kiểm tra lại các lỗi đã sửa, xác nhận không còn lỗi chặn demo trong phạm vi đã chọn.
- [ ] Hòa hoàn thiện Spec Kit artifacts phản ánh đúng quá trình và phiên bản bàn giao.
- [ ] Nhóm có source, hướng dẫn chạy, bằng chứng kiểm thử và video có thuyết minh để nộp.
