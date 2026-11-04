# PA3 — Kế hoạch kỹ thuật và hợp đồng API cho demo quản lý tài khoản
> *Soạn thảo: Nguyễn Lê Đức Nhật (AI hỗ trợ) | Phụ trách Spec Kit: Hòa | Rà soát: Hòa, Nhật, Đạt, Khiêm — chưa xác nhận*

Ngày lập: 04/11/2026. Thực hiện **bước 3 — Lập plan và hợp đồng API** trong [Agent.md](Agent.md). Đây là kế hoạch triển khai bằng tiếng Việt để nhóm phối hợp, chưa phải bằng chứng code đã hoàn thành hoặc kế hoạch đã được nhóm duyệt. Hòa đưa nội dung đã thống nhất vào quy trình Spec Kit; bản nộp báo cáo cần tiếng Anh theo yêu cầu PA3.
\
## 1. Phạm vi và hiện trạng đã kiểm tra
> *Soạn thảo: Nhật | Rà soát: Hòa, Đạt, Khiêm — chưa xác nhận*

Phạm vi: đăng ký, đăng nhập, xem/sửa tên hiển thị và số điện thoại, refresh token, đăng xuất phiên hiện tại. Email chỉ đọc sau đăng ký. Không triển khai quên/đổi mật khẩu, đổi email, upload avatar, admin web và nghiệp vụ tài chính trong đợt demo này.

| Thành phần hiện có | Hướng xử lý |
|---|---|
| [Android](src/mobile/README.md): Kotlin, Jetpack Compose, compile SDK 37, min SDK 26 | Dùng làm client demo thật; không dùng FE React/Figma của nhánh tài liệu cũ. |
| [AuthRepository](src/mobile/app/src/main/java/com/finsync/auth/AuthRepository.kt) đang dùng `UnavailableAuthRepository` | Đạt bổ sung adapter gọi HTTP, session manager và kết quả lỗi phù hợp API. |
| Đăng ký hiện nhận email/mật khẩu; chưa nhận tên | Đạt thêm tên hiển thị, giữ xác nhận mật khẩu ở client; Nhật nhận `displayName` trong API. |
| [Backend](src/backend/pom.xml): Java 21, Maven Wrapper, Spring Boot 4.1.1 được khai báo trong pom | Nhật kiểm tra build bằng wrapper trước khi thêm code; không mặc định đổi về Boot 3 theo tài liệu cũ. Phiên bản khai báo chưa được kiểm chứng build trong lần lập plan này. |
| Có JPA, Security, Validation, Flyway và PostgreSQL dependency | Tận dụng khung hiện tại; bổ sung hỗ trợ JWT/Redis và thư viện kiểm thử cần thiết. |
| [Docker Compose](docker-compose.yml) có PostgreSQL 16 và Backend, chưa có Redis | Nhật thêm Redis, healthcheck và biến môi trường. |
| Cấu hình hiện dùng `ddl-auto=update`, Flyway bật, thời hạn JWT 24 giờ | Chuyển sang Flyway quản lý schema, `ddl-auto=validate`; thống nhất lại TTL theo mục 5. |
| [Database design trong nhánh](docs/analysis-and-design/database-design.md) hiện chưa có nội dung | Nhật đưa bản thiết kế `users`/`auth_sessions` từ nhánh tài liệu vào vị trí chuẩn trước khi migration. Không coi đường dẫn `database_design.md` trong Agent.md là file đang tồn tại ở nhánh này. |
| Đã có `src/.specify/`, chưa thấy `spec.md`/`plan.md` tính năng qua danh sách file đã kiểm tra | Hòa kiểm tra cấu hình Spec Kit hiện có, tạo/hoàn thiện spec và chọn thư mục feature thống nhất. |

## 2. Phân công và đầu ra bàn giao
> *Soạn thảo: Nhật | Rà soát: Hòa, Đạt, Khiêm — chưa xác nhận*

| Thành viên | Chịu trách nhiệm chính | Đầu ra |
|---|---|---|
| **Hòa — Spec Kit** | Quản lý spec, plan, tasks; đối chiếu yêu cầu PA3/Use Case; ghi nhận quyết định và thay đổi phạm vi. | Spec với tiêu chí nghiệm thu, plan đồng bộ tài liệu này, tasks có phụ thuộc và liên kết công việc của từng người. |
| **Nhật — Backend** | Database/migration/entity, Security, API, lỗi, môi trường chạy và tích hợp server. | API theo mục 4; migration; hướng dẫn chạy; kiểm thử nghiệp vụ của BE; dữ liệu thử. |
| **Đạt — Frontend** | Màn hình Android, HTTP adapter, lưu phiên an toàn, refresh, điều hướng và lỗi. | Luồng thao tác thật từ đăng ký đến logout; hồ sơ lấy từ API; cấu hình URL theo môi trường. |
| **Khiêm — Testing** | Lập test matrix từ spec/API, kiểm thử độc lập API và Android end-to-end, báo lỗi và xác nhận kiểm tra lại. | Kịch bản, dữ liệu thử, kết quả thực tế, bằng chứng lỗi/đạt và checklist demo. |

Nhật và Đạt vẫn kiểm tra phần mình trước khi bàn giao; Khiêm phụ trách kiểm thử tổng thể. Hòa điều phối tài liệu Spec Kit, không phải người duy nhất quyết định thiết kế kỹ thuật. Người rà soát chưa được ghi là đã duyệt khi chưa thực hiện.

## 3. Kiến trúc và dữ liệu
> *Soạn thảo: Nhật | Rà soát: Hòa, Đạt, Khiêm — chưa xác nhận*

Luồng: Android Compose → ViewModel → repository HTTP → Spring Security → controller/DTO → service transaction → JPA/PostgreSQL. Redis lưu blacklist access token. API dùng JSON camelCase; database dùng snake_case. Không trả entity trực tiếp.

Nhật tổ chức code trong `src/backend/src/main/java/com/finsync/`: `config`, `security`, `controller`, `dto`, `service`/`service.impl`, `repository`, `model/entity`, `exception`. Flyway đặt trong `src/backend/src/main/resources/db/migration/`.

| Bảng / entity | Field cần cho thiết kế tài khoản |
|---|---|
| `users` / `User` | `id` UUID; `created_at`, `updated_at` UTC; `version` bigint; `email` varchar(254) UNIQUE sau trim/lowercase; `password_hash` varchar(255); `display_name` varchar(100); `phone_number` varchar(30) nullable; `avatar_key` nullable; `default_currency` mặc định VND; `time_zone` mặc định Asia/Ho_Chi_Minh; `role` USER/ADMIN; `status` ACTIVE/LOCKED; `locked_at`, `lock_reason` nullable theo trạng thái. |
| `auth_sessions` / `AuthSession` | Bốn cột chung như users; `user_id` FK NOT NULL → users.id; `refresh_token_hash` varchar(64) UNIQUE; `client_type` MOBILE cho demo này; `device_label` varchar(200) nullable; `expires_at`, `last_used_at`, `revoked_at`. |

User 1:N AuthSession, FK dùng RESTRICT. Nhật đối chiếu kiểu/độ dài/NULL đầy đủ với bản thiết kế bàn giao trước khi tạo migration. Mật khẩu dùng BCrypt; refresh token ngẫu nhiên chỉ lưu SHA-256 hash. `version` là optimistic locking, không phải phiên bản API. Mặc định đăng ký tạo USER/ACTIVE; không cho client tự đặt quyền/trạng thái.

Migration tạo hai bảng và index cần thiết. Không cần tạo toàn bộ 22 bảng cho demo. Chạy migration trên database phát triển riêng; không tự reset hoặc baseline một database có dữ liệu mà chưa kiểm tra schema. Bỏ cơ chế tự cập nhật schema của Hibernate sau khi dùng migration.

## 4. Hợp đồng API v1
> *Soạn thảo: Nhật | Rà soát tích hợp: Đạt | Rà soát kiểm thử: Khiêm — chưa xác nhận*

Base path: `/api/v1`. Content-Type: `application/json`. Request bảo vệ dùng `Authorization: Bearer <accessToken>`. Thời gian JSON dùng ISO-8601 UTC; UUID là chuỗi. Response thành công trả đối tượng trực tiếp, không thêm lớp `data`. Endpoint chứa token trả `Cache-Control: no-store`.

| Method / path | Request | Thành công | Lỗi chính |
|---|---|---|---|
| `POST /auth/register` | `{displayName,email,password}` | **201**: `{id,email,displayName}`; không tự đăng nhập. | 400 dữ liệu sai; 409 EMAIL_ALREADY_EXISTS. |
| `POST /auth/login` | `{email,password,rememberMe?,deviceLabel?}` | **200**: TokenPair bên dưới. `rememberMe` mặc định false; server gán client_type=MOBILE. | 401 INVALID_CREDENTIALS; 403 ACCOUNT_LOCKED sau khi kiểm tra đúng thông tin đăng nhập. |
| `POST /auth/refresh` | `{refreshToken}`; không cần access token | **200**: TokenPair mới, thay refresh token cũ. | 401 INVALID_REFRESH_TOKEN cho token sai/hết hạn/đã xoay/phiên thu hồi; 403 ACCOUNT_LOCKED. |
| `POST /auth/logout` | Không body; Bearer token | **204**, không body; thu hồi phiên trong claim `sid`. | 401 khi không có xác thực hợp lệ; client xử lý phiên đã kết thúc. |
| `GET /users/me` | Không body; Bearer token | **200**: UserProfile bên dưới. | 401 UNAUTHORIZED; 403 ACCOUNT_LOCKED. |
| `PATCH /users/me` | `{version,displayName?,phoneNumber?}`; Bearer token | **200**: UserProfile mới với version mới. | 400 VALIDATION_ERROR; 409 PROFILE_VERSION_CONFLICT; 401/403 như trên. |

TokenPair:

```json
{
  "tokenType": "Bearer",
  "accessToken": "<jwt>",
  "expiresIn": 900,
  "refreshToken": "<opaque-token>",
  "refreshExpiresAt": "2026-11-11T10:00:00Z"
}
```

UserProfile:

```json
{
  "id": "1733f7f5-5f28-4532-9e83-683c62e033ec",
  "email": "demo@example.com",
  "displayName": "Demo User",
  "phoneNumber": null,
  "version": 0
}
```

Quy tắc validation và cập nhật:

- Email bắt buộc, đúng định dạng, tối đa 254 ký tự, trim và lowercase ở server. Ràng buộc UNIQUE xử lý cả hai request đăng ký đồng thời.
- `displayName`: trim, 1–100 ký tự. Đăng ký phải có; PATCH bỏ qua field nghĩa là giữ nguyên, null/chuỗi rỗng là lỗi.
- Password đăng ký: ít nhất 8 ký tự và tối đa **72 byte UTF-8** để phù hợp BCrypt; không trim, không cắt ngầm. Đây là điều chỉnh đề xuất so với FE đang giới hạn 128 ký tự; Đạt đồng bộ kiểm tra và thông báo. Login không áp lại yêu cầu tối thiểu dành cho tạo mật khẩu.
- `phoneNumber`: nullable, trim, tối đa 30 ký tự; cho phép chữ số và các ký hiệu `+`, khoảng trắng, `-`, `(`, `)` với ít nhất một chữ số. Không dùng làm định danh hoặc coi là số đã xác minh. PATCH bỏ qua giữ nguyên; null hoặc chuỗi rỗng sau trim là xóa số điện thoại.
- PATCH bắt buộc `version` số nguyên không âm và ít nhất một trường sửa. Nếu version cũ: trả 409, FE tải lại để người dùng xem và xác nhận, không tự ghi đè. Service kiểm tra version và dùng `@Version` để bắt cả cập nhật đồng thời sau khi đọc.
- Field không được phép như `role`, `status`, `email`, `userId`, `password` trong PATCH trả 400. Xác định user từ JWT `sub`, không từ body.
- `rememberMe` là boolean; `deviceLabel` tối đa 200 ký tự. Không gửi xác nhận mật khẩu đến BE; FE so khớp trước khi gọi register.

Lỗi thống nhất cho cả controller và Security:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Invalid request",
  "fieldErrors": {"displayName": "Must not be blank"},
  "traceId": "request-correlation-id"
}
```

Lỗi không gắn field dùng `fieldErrors: {}`. FE ánh xạ `code` sang thông báo, không phụ thuộc chuỗi `message`. Không trả stack trace hoặc token. Sai tài khoản/mật khẩu dùng cùng lỗi 401. Redis không truy cập được khi kiểm tra bảo mật trả **503 AUTH_SERVICE_UNAVAILABLE**, không coi là sai mật khẩu hoặc tự logout vì lỗi dịch vụ. Lỗi server còn lại trả 500 INTERNAL_ERROR.

## 5. JWT, refresh, logout và Android session
> *Soạn thảo: Nhật | Phối hợp: Đạt, Khiêm | Rà soát: Chờ xác nhận*

- Access JWT hết hạn sau **15 phút**, chứa `sub`, `sid`, `jti`, `iat`, `exp`, issuer và audience. Nhật chọn cấu hình ký HS256 với khóa ngẫu nhiên tối thiểu 256 bit từ môi trường, khóa thuật toán server và kiểm tra chữ ký/issuer/audience/thời gian. Không dùng khóa mẫu có sẵn làm bí mật triển khai.
- Refresh token opaque ngẫu nhiên tối thiểu 256 bit; phiên có hạn tuyệt đối **7 ngày**, hoặc **30 ngày** khi rememberMe. Refresh không kéo dài hạn phiên. Cập nhật hash bằng transaction và khóa phù hợp; một refresh token chỉ đổi được một lần, kể cả gọi đồng thời.
- API bảo vệ kiểm tra user ACTIVE, session tồn tại/chưa thu hồi/chưa hết hạn và blacklist `jti`. Khóa user rồi session khi thao tác cần cả hai; refresh phải kiểm tra lại trạng thái ngay trong transaction.
- Logout đặt `revoked_at` và blacklist JWT hiện tại đến khi JWT hết hạn. Chặn phiên là cơ chế thu hồi mọi access token của phiên. Nếu Redis lỗi sau khi đã commit thu hồi phiên, giữ nguyên trạng thái thu hồi, ghi lỗi để xử lý; không phục hồi phiên. Không tuyên bố PostgreSQL và Redis có cùng transaction.
- Android giữ access token trong bộ nhớ; refresh token lưu dưới dạng mã hóa với khóa được bảo vệ bởi Android Keystore, không lưu token trong log, URL hoặc preferences dạng rõ. Logout xóa dữ liệu phiên cục bộ sau xử lý kết quả; mất mạng phải báo chưa xác nhận thu hồi trên server, không giả báo đã thu hồi.
- Đạt cho refresh chạy một lần tại một thời điểm: nhiều API cùng gặp 401 phải chờ cùng một lần refresh. Không refresh lại cho chính endpoint login/refresh; request gốc chỉ retry một lần. Nếu refresh trả 401/403, xóa phiên và về login; lỗi mạng/503 cho phép thử lại, không tạo vòng lặp.
- Khi app khởi động lại: nếu có refresh token, khôi phục phiên rồi gọi `/users/me`. Nếu phản hồi refresh bị mất sau server commit, token cũ có thể không còn hợp lệ: yêu cầu login lại, không nới lỏng chống replay để giấu tình huống này.
- Demo là native Android: refresh token gửi bằng JSON, không dùng cookie trình duyệt hoặc quy tắc `ADMIN_WEB` trong kế hoạch cũ. Hướng dẫn HTTP cleartext chỉ dành cho debug local; bản release dùng HTTPS.

## 6. Tích hợp FE và môi trường chạy
> *Soạn thảo: Nhật | Thực hiện: Nhật, Đạt | Kiểm thử: Khiêm*

Đạt bổ sung HTTP client Retrofit/OkHttp, DTO camelCase, repository thật và session manager. Mở rộng AuthResult/AuthFailure để biểu diễn lỗi field, email trùng, đăng nhập sai, khóa tài khoản, phiên hết hạn và dịch vụ lỗi. Cập nhật controller/ViewModel tương ứng, không chỉ thay tên repository.

Sau register thành công quay về login với email điền sẵn. Sau login gọi `/users/me` để mở hồ sơ. Màn hình hồ sơ có tên, email chỉ đọc, số điện thoại, nút lưu, loading, lỗi và logout. Không hiển thị kết quả thành công trước response API. Workspace mẫu vẫn ghi rõ là dữ liệu mẫu; không dùng dữ liệu tài chính giả làm bằng chứng đã tích hợp backend.

| Môi trường | Địa chỉ / yêu cầu |
|---|---|
| BE chạy trên máy host | Cổng 8080; cấu hình DB/Redis bằng biến môi trường. |
| Android Emulator chuẩn | Base URL debug `http://10.0.2.2:8080/api/v1/`; localhost trong emulator không trỏ về máy phát triển. |
| Điện thoại thật | URL IP LAN của máy BE, cùng mạng và mở cổng cần thiết; cấu hình riêng, không hardcode IP cá nhân vào source. |
| Compose | PostgreSQL 16, Redis và Backend cùng network; Backend dùng tên service để kết nối DB/Redis. |

Nhật bổ sung Redis vào Compose và cấu hình mẫu không chứa bí mật; bỏ fallback password/JWT secret đang có trong cấu hình. Chốt `JAVA_HOME` Java 21 cho BE; Android dùng JDK/Gradle của project mobile. Không đồng nhất JDK runtime BE với mức Java target của Android.

Nhật cung cấp hướng dẫn chạy/migrate; Đạt cung cấp cấu hình URL debug, quyền Internet và cách build/install; Khiêm chạy lại từ môi trường sạch. Cả nhóm giữ credentials thử ngoài Git và không quay giá trị token trong video.

## 7. Thứ tự bàn giao và phụ thuộc
> *Soạn thảo: Nhật | Điều phối Spec Kit: Hòa | Rà soát: Cả nhóm — chưa xác nhận*

| Mốc | Người chính | Công việc và điều kiện bàn giao |
|---|---|---|
| P1 — Đồng bộ spec/plan | Hòa; Nhật, Đạt, Khiêm rà soát | Ghi rõ phạm vi, hợp đồng API ở trên và tiêu chí nghiệm thu; đối chiếu Use Case gốc. Xác nhận các lựa chọn password, TTL, profile version và tên JSON trước triển khai. |
| P2 — Chia tasks | Hòa | Tạo tasks từ plan với phụ thuộc và gán người trên Jira. Tài liệu này là đầu vào bước 3, không thay cho việc dùng Spec Kit thực tế. |
| P3 — Nền chạy và database | Nhật | Kiểm tra build, khôi phục thiết kế tài khoản vào nhánh, thêm môi trường Redis, Flyway, entity/repository và lỗi chung. |
| P4 — Xác thực | Nhật | Register → login/JWT → refresh/logout/blacklist; bàn giao request/response mẫu và cách khởi động cho FE/Testing. |
| P5 — Hồ sơ | Nhật | GET/PATCH me, quyền sở hữu và version conflict; triển khai sau auth. |
| P6 — Android | Đạt | Sau P1 có thể làm màn hình, DTO và adapter với mock theo contract; sau P4/P5 nối API thật, session và lỗi. |
| P7 — Kiểm thử | Khiêm | Soạn matrix từ P1; kiểm tra API ngay khi P4/P5 bàn giao; chạy end-to-end sau P6. Nhật/Đạt sửa lỗi và Khiêm kiểm tra lại. |
| P8 — Chốt demo | Cả nhóm | Hòa cập nhật artifacts; Nhật/Đạt hỗ trợ môi trường; Khiêm xác nhận kịch bản. Nhóm phân công người thuyết minh và upload video. |

Nếu phát hiện contract khác spec, người phát hiện báo Hòa và bên liên quan để cập nhật spec/plan/tasks trước khi triển khai phần thay đổi. Không âm thầm đổi field hoặc status khiến FE/BE lệch nhau. Phần code nền đã tồn tại được ghi nhận là có sẵn, không mô tả rằng mới được sinh bởi Spec Kit.

## 8. Kiểm thử và điều kiện hoàn thành bước triển khai
> *Soạn thảo: Nhật | Phụ trách Testing: Khiêm | Phối hợp: Nhật, Đạt, Hòa*

| Nhóm kiểm thử | Kết quả cần chứng minh |
|---|---|
| Migration và lưu trữ | Chạy từ DB sạch; schema đúng; mật khẩu/refresh lưu hash; dữ liệu hồ sơ còn sau restart Backend. |
| Register | Hợp lệ tạo đúng một USER/ACTIVE; dữ liệu sai trả 400; email khác hoa/thường và hai request đồng thời không tạo trùng. Kiểm tra giới hạn password theo byte UTF-8. |
| Login/JWT | Đúng tài khoản vào hồ sơ; sai thông tin 401; khóa tài khoản bị chặn; token giả, sai issuer/audience hoặc hết hạn không qua được. |
| Refresh | Cấp cặp token mới; token cũ và hai refresh đồng thời chỉ có tối đa một lần thành công; không kéo dài hạn tuyệt đối. |
| Hồ sơ | Sửa đúng tài khoản từ token; không sửa được role/status/email; null phone xóa, field bỏ qua giữ nguyên; version cũ trả 409. |
| Logout/blacklist | Phiên cũ không gọi API/refresh được sau logout; phiên khác không bị thu hồi nhầm; `jti` trong blacklist bị chặn; Redis lỗi không được bỏ qua kiểm tra. |
| Android | UI loading/lỗi rõ ràng; không lặp refresh; mở lại app khôi phục được phiên hợp lệ; lỗi mạng không báo đã lưu; thay tài khoản không hiển thị hồ sơ user cũ. |
| End-to-end | Đăng ký → login → GET hồ sơ → PATCH → mở lại và thấy dữ liệu mới → logout → không truy cập API bảo vệ bằng phiên cũ. |

Nhật viết kiểm thử service/Security/integration phù hợp với PostgreSQL/Redis; Đạt kiểm tra ViewModel/session và màn hình; Khiêm quản lý matrix, chạy API collection và luồng Android, ghi expected/actual, môi trường và bằng chứng. Không dùng H2 thay thế duy nhất cho kiểm tra ràng buộc PostgreSQL. Test dùng TTL ngắn qua cấu hình riêng khi cần, không đổi TTL production để quay demo.

**Hoàn thành demo khi:** contract đã được đồng bộ trong Spec Kit; API và Android chạy thật; các tình huống chính được Khiêm kiểm tra và lỗi chặn đã sửa; có hướng dẫn chạy, source, migration và artifacts. Video có thuyết minh thể hiện luồng end-to-end, đăng YouTube Unlisted/Public và đưa link vào bài nộp. Không coi ảnh prototype hay kết quả kiểm thử cũ là bằng chứng chức năng mới đã chạy.

## 9. Trạng thái của lần lập kế hoạch này
> *Soạn thảo: Nhật (AI hỗ trợ) | Rà soát: Chờ nhóm xác nhận*

Đã đọc Agent.md, cấu trúc Android/auth hiện có, pom Backend, cấu hình JPA/Flyway/JWT, Compose và vị trí Spec Kit để lập plan. Chưa chạy build, migration, API, Redis hoặc kiểm thử end-to-end; chưa khởi chạy Spec Kit và chưa tạo entity/API trong bước này. Kế hoạch là đầu vào cụ thể để Hòa, Nhật, Đạt và Khiêm tiếp tục phần việc của mình.
