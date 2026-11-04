# Kế hoạch hoàn thiện tính năng demo PA3 — Quản lý tài khoản
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ soạn thảo) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ nhóm xác nhận*

Tài liệu phối hợp nội bộ bằng tiếng Việt, mô tả thứ tự thực hiện từ chốt yêu cầu đến quay demo. Đây là kế hoạch đề xuất, chưa phải xác nhận tính năng đã triển khai. Tài liệu nộp chính thức của PA3 vẫn cần tuân thủ yêu cầu tiếng Anh.

Nguồn đối chiếu: [Đề PA3](PA/PA3/pa3_project_assignment_specification.md), [thiết kế database](database_design.md), [Use Case tài khoản/admin](PA/PA3/use-cases/admin-account-use-cases.md) và [hướng dẫn Spec Kit](https://github.github.com/spec-kit/quickstart).

## 1. Phạm vi demo cần chốt
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ soạn thảo) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ nhóm xác nhận*

Nhóm chức năng: **Quản lý tài khoản — đăng ký, đăng nhập, xem và cập nhật hồ sơ**. Bổ sung refresh token và đăng xuất để hoàn thiện vòng đời phiên đăng nhập theo thiết kế bảo mật hiện có.

| Chức năng | Kết quả cần đạt |
|---|---|
| Đăng ký | Tạo tài khoản USER với email duy nhất, tên hiển thị và mật khẩu đã băm. |
| Đăng nhập | Kiểm tra thông tin và trạng thái tài khoản; cấp JWT access token, refresh token và lưu phiên. |
| Xem hồ sơ | Trả về thông tin của tài khoản đang được xác thực. |
| Cập nhật hồ sơ | Cho sửa tên hiển thị, số điện thoại; tải lại vẫn thấy dữ liệu đã lưu. |
| Refresh token | Cấp lại access token khi phiên còn hợp lệ; xoay refresh token để token cũ không dùng lại được. |
| Đăng xuất | Thu hồi phiên hiện tại; token của phiên đó không tiếp tục truy cập API bảo vệ. |

Đề xuất email chỉ đọc trong hồ sơ. Đổi email, đổi mật khẩu, quên mật khẩu, upload avatar, admin web và các nghiệp vụ tài chính chưa nằm trong đợt demo này. Việc thu gọn demo không xóa các yêu cầu đó khỏi phạm vi toàn dự án.

**Tiêu chí xuyên suốt:** Người dùng thao tác trên FE → API xử lý thật → PostgreSQL lưu dữ liệu thật → FE hiển thị kết quả từ API. Màn hình chạy bằng dữ liệu giả chưa đáp ứng yêu cầu end-to-end của mục E.

## 2. Phân công và cách phối hợp với Spec Kit
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ soạn thảo) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ nhóm xác nhận*

| Vai trò đề xuất | Công việc |
|---|---|
| Leader / cả nhóm | Chốt phạm vi, phân công Jira, thống nhất tiêu chí hoàn thành và rà soát kết quả. |
| Thành viên phụ trách Spec Kit | Khởi tạo công cụ; tạo và duy trì spec, plan, tasks; đối chiếu với FE/BE. |
| Nguyễn Lê Đức Nhật — Backend | Thiết kế hợp đồng API, migration, entity, Security, service, controller và kiểm thử Backend. |
| Thành viên phụ trách FE | Rà soát màn hình hiện có, nối API, xử lý trạng thái và lỗi, quản lý phiên phía client. |
| Người phụ trách tích hợp / demo | Chạy luồng hoàn chỉnh, chuẩn bị dữ liệu demo, quay video và tổng hợp bài nộp. |

Một người có thể kiêm nhiều vai trò; nhóm cần gán người thực hiện cụ thể cho từng task.

**Spec Kit làm gì?** Công cụ hỗ trợ phát triển theo đặc tả: Specify → Plan → Tasks → Implement. Các thành viên vẫn phải đọc, chỉnh và kiểm tra tài liệu/code được tạo.

| Bước Spec Kit | Nội dung cho tính năng này | Đầu ra chính |
|---|---|---|
| Specify | Người dùng làm gì, dữ liệu hợp lệ ra sao, các nhánh lỗi và tiêu chí nghiệm thu. | `spec.md` |
| Plan | Cách dùng FE hiện có, Spring Boot, PostgreSQL, Redis, JWT; thiết kế dữ liệu và API. | `plan.md` và tài liệu thiết kế liên quan |
| Tasks | Chia việc theo thứ tự phụ thuộc, nêu việc FE/BE có thể làm song song. | `tasks.md` |
| Implement | Triển khai theo task, cập nhật tiến độ, kiểm tra với spec và sửa chỗ chưa khớp. | Source code và kết quả kiểm tra |

Các file được giữ trong thư mục tính năng do Spec Kit thiết lập trong repository. Dùng công cụ thực tế để tạo và duy trì chúng; tên lệnh cụ thể phụ thuộc phiên bản và AI agent tích hợp.

**Backend có thể chuẩn bị project trong khi thành viên khác làm Spec Kit.** Tuy nhiên, nên chốt spec, plan và các task liên quan trước khi triển khai nghiệp vụ. Không đợi code xong toàn bộ mới tạo tài liệu rồi mô tả ngược rằng Spec Kit đã dẫn dắt các bước đó. Phần code có sẵn được ghi nhận làm đầu vào để lập kế hoạch hoàn thiện tiếp.

## 3. Thứ tự thực hiện
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ soạn thảo) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ nhóm xác nhận*

| Bước | Việc cần làm | Đầu ra / điều kiện chuyển bước |
|---|---|---|
| 1. Chốt phạm vi | Cả nhóm duyệt mục 1, kiểm tra Use Case tài khoản và màn hình FE; tạo task và phân công trước khi thực hiện. | Danh sách chức năng, các trường hồ sơ được sửa và tiêu chí nghiệm thu thống nhất. |
| 2. Tạo spec | Người phụ trách Spec Kit đặc tả luồng chính, lỗi, quyền truy cập và các giới hạn. FE/BE cùng rà soát. | `spec.md` đủ rõ để biết thế nào là làm đúng. |
| 3. Lập plan và hợp đồng API | Thống nhất stack, cấu trúc, migration, token phía client, endpoint, request/response và định dạng lỗi. | `plan.md`, hợp đồng API và quyết định kỹ thuật khớp database design. |
| 4. Chia tasks | Tách task database, Security, từng API, từng màn hình tích hợp và kiểm thử. | `tasks.md` có thứ tự phụ thuộc và người nhận việc trong công cụ quản lý của nhóm. |
| 5. Dựng nền Backend | Khởi tạo Spring Boot, cấu hình môi trường, PostgreSQL và Redis; chuẩn bị migration và xử lý lỗi chung. | Backend khởi động, kết nối được các dịch vụ; không commit bí mật. |
| 6. Tạo migration và entity | Tạo `users`, `auth_sessions`, FK, unique email/hash, index và version; mapping entity/repository. | Migration chạy trên database sạch; entity khớp thiết kế. |
| 7. Triển khai đăng ký | DTO validation, chuẩn hóa email, băm mật khẩu, tạo USER/ACTIVE và xử lý email trùng. | API tạo user thật; response không lộ mật khẩu/hash. |
| 8. Triển khai xác thực | Login, JWT filter, kiểm tra trạng thái user/phiên, refresh rotation, logout và blacklist Redis. | Token hợp lệ truy cập được; token hết hạn/thu hồi hoặc user bị khóa bị chặn. |
| 9. Triển khai hồ sơ | `GET /users/me`, `PATCH /users/me`; chỉ cập nhật trường cho phép. | User xem/sửa đúng hồ sơ của mình, không thể tự đổi role/status hoặc sửa tài khoản khác. |
| 10. Nối FE | Thay dữ liệu giả bằng API, xử lý loading/lỗi, refresh phiên và logout. FE có thể làm trước các phần không phụ thuộc API đang hoàn thiện. | Luồng đầy đủ chạy từ giao diện, dữ liệu tồn tại sau tải lại. |
| 11. Kiểm thử tích hợp | Chạy các kịch bản ở mục 6; đối chiếu spec, sửa lỗi và ghi lại kết quả thực tế. | Các luồng trong phạm vi đã chọn hoạt động ổn định. |
| 12. Quay và nộp demo | Quay có thuyết minh, đưa lên YouTube; gom source, Spec Kit artifacts và tài liệu báo cáo. | Link xem được, bộ nộp đủ và không chứa thư mục dependency/build. |

**Phần Nhật nên bắt đầu:** rà soát `User`/`AuthSession` và đề xuất API để người phụ trách Spec Kit đưa vào plan; đồng thời dựng khung Backend. Sau khi thống nhất plan/tasks, triển khai migration và entity rồi lần lượt đến Security, service, controller. Không cần tạo cả 22 entity ngay cho demo tài khoản.

## 4. Database và Backend cần chuẩn bị
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ soạn thảo) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ nhóm xác nhận*

| Thành phần | Vai trò trong demo |
|---|---|
| `users` / `User` | Tài khoản, password hash, hồ sơ, role và status. Giữ các cột chung, default và ràng buộc trong database design. |
| `auth_sessions` / `AuthSession` | Phiên thuộc user, refresh token hash, hạn phiên và trạng thái thu hồi. Quan hệ User 1:N AuthSession. |
| Redis | Lưu blacklist JWT theo `jti` với TTL còn lại của token; không tạo thêm bảng PostgreSQL. |
| DTO | Tách dữ liệu request/response khỏi entity; không trả password hash hoặc refresh hash. |
| Service | Thực thi nghiệp vụ, quyền sở hữu và transaction. |
| Controller / Security | Nhận request, xác thực/phân quyền, trả response và lỗi thống nhất. |

Đọc chi tiết mục 3.1, 3.2 và 5.3 của [database_design.md](database_design.md) trước khi mapping. `version` dùng kiểm soát cập nhật đồng thời, không phải phiên bản API. Refresh token chỉ lưu hash; JWT access token không lưu nguyên văn trong bảng.

Với FE web, cần chốt cách giữ token trước khi nối API: đề xuất access token trong bộ nhớ, refresh token trong cookie HttpOnly/Secure với chính sách SameSite, CORS và CSRF/Origin phù hợp môi trường. Không tự gắn client web demo thành `ADMIN_WEB` nếu phục vụ user thường: enum hiện có `MOBILE`/`ADMIN_WEB`, nên plan phải xác định đây là client giả lập mobile hay cần bổ sung `USER_WEB` và cập nhật thiết kế tương ứng.

## 5. Hợp đồng API đề xuất
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ soạn thảo) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ nhóm xác nhận*

Đây là danh sách để FE/BE chốt trong plan; chưa phải endpoint đã tồn tại. Có thể dùng tiền tố chung `/api/v1`.

| Method / endpoint | Chức năng | Xác thực |
|---|---|---|
| `POST /auth/register` | Nhận tên, email, mật khẩu; tạo user, sau đó chuyển sang đăng nhập. | Không cần access token. |
| `POST /auth/login` | Kiểm tra email/mật khẩu; tạo phiên, cấp token. | Không cần access token. |
| `POST /auth/refresh` | Kiểm tra refresh token/phiên; cấp token mới và xoay refresh token. | Refresh token hợp lệ. |
| `POST /auth/logout` | Thu hồi phiên hiện tại và xóa thông tin phiên phía client. | Phiên được xác thực theo hợp đồng đã chốt. |
| `GET /users/me` | Trả hồ sơ của user từ danh tính đã xác thực. | Access token hợp lệ. |
| `PATCH /users/me` | Cập nhật `display_name`, `phone_number`. | Access token hợp lệ. |

Phải chốt thêm trong hợp đồng: tên JSON field, trường bắt buộc, giới hạn độ dài, cách xóa số điện thoại, cấu trúc lỗi, HTTP status và cách truyền token. Đề xuất lỗi validation là 400, thông tin đăng nhập/token không hợp lệ là 401, không đủ quyền là 403 và email trùng là 409. Không lấy `user_id` do client gửi làm căn cứ quyết định chủ hồ sơ.

## 6. Kiểm tra trước khi quay
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ soạn thảo) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ nhóm xác nhận*

| Kịch bản | Kết quả mong đợi |
|---|---|
| Đăng ký hợp lệ | Tạo đúng một user; mật khẩu được băm; mặc định USER/ACTIVE. |
| Email trùng, kể cả khác chữ hoa/thường | Không tạo tài khoản trùng sau chuẩn hóa; FE hiển thị lỗi rõ ràng. |
| Thiếu hoặc sai định dạng dữ liệu | Backend từ chối và FE chỉ ra lỗi; không chỉ dựa vào validation phía FE. |
| Sai mật khẩu hoặc tài khoản bị khóa | Không cấp phiên/token hợp lệ. |
| Đăng nhập đúng | FE vào được màn hình hồ sơ; API trả dữ liệu đúng tài khoản. |
| Sửa tên/số điện thoại rồi tải lại | Dữ liệu mới vẫn còn vì đã lưu PostgreSQL. |
| Gửi thêm `role`, `status`, `user_id` trong cập nhật | Không thay đổi quyền, trạng thái hoặc hồ sơ của người khác. |
| Thiếu token, JWT giả hoặc token hết hạn | API bảo vệ từ chối. |
| Refresh hợp lệ rồi dùng lại refresh token cũ | Token mới hoạt động; refresh token cũ không được chấp nhận. |
| Logout rồi gọi API bằng token cũ | Phiên đã thu hồi bị chặn; không refresh được phiên đó. |
| JWT có `jti` bị blacklist | API bảo vệ từ chối dù chữ ký và thời hạn JWT còn hợp lệ. |
| FE gọi API lỗi hoặc mất kết nối | Không báo thành công giả; người dùng có thể xử lý/thử lại phù hợp. |

Đề PA3 không bắt buộc rà soát/tinh chỉnh các test case được Spec Kit sinh ra ở giai đoạn này. Tuy nhiên, nhóm vẫn cần kiểm tra luồng chạy thực tế để bảo đảm demo end-to-end; không ghi “đã pass” khi chưa chạy.

## 7. Kịch bản quay demo và bộ nộp
> *Thực hiện: Nguyễn Lê Đức Nhật (AI hỗ trợ soạn thảo) | Rà soát: Chờ nhóm xác nhận | Chỉnh sửa: Chờ nhóm xác nhận*

Kịch bản gợi ý:

1. Giới thiệu phạm vi nhóm chức năng và FE → Backend → PostgreSQL; nêu Redis phục vụ blacklist.
2. Giới thiệu ngắn spec, plan, tasks và cách nhóm sử dụng trong quá trình triển khai.
3. Đăng ký tài khoản mới trên FE; minh họa email trùng hoặc dữ liệu sai.
4. Thử đăng nhập sai, sau đó đăng nhập đúng.
5. Mở hồ sơ, sửa tên/số điện thoại, tải lại để chứng minh dữ liệu được lưu.
6. Minh họa refresh token bằng môi trường test phù hợp và logout; kiểm tra phiên đã logout không truy cập API bảo vệ. Không phô token hay bí mật trong video.
7. Kết thúc bằng phạm vi đã hoàn thành và các giới hạn thực tế.

**Bắt buộc theo mục E:** video có thuyết minh, upload YouTube ở chế độ Unlisted hoặc Public và đưa link vào bài nộp; nộp source đầy đủ cùng Spec Kit artifacts. Đề không nêu thời lượng video, yêu cầu webcam hoặc ngôn ngữ thuyết minh cụ thể.

Trước khi đóng gói:

- Kiểm tra link video xem được bằng tài khoản khác.
- Có hướng dẫn chạy FE/BE, cấu hình mẫu không chứa bí mật và migration database.
- Có các file spec, plan, tasks phản ánh đúng quá trình và phạm vi đã thực hiện.
- Loại bỏ `node_modules`, `venv`, build artifacts và các thư mục sinh tự động không cần nộp.
- Cập nhật AI Usage Report, tiến độ Jira, Git log và phần báo cáo tương ứng theo yêu cầu PA3.
- Tài liệu nộp chính thức bằng tiếng Anh, có Markdown và PDF; đóng gói theo tên `PA3-Group[GroupId].zip`.

**Điều kiện hoàn thành tính năng demo:** Luồng đăng ký → đăng nhập → xem/sửa hồ sơ → tải lại → đăng xuất chạy được trên FE với API và dữ liệu thật; các kiểm tra xác thực chính đạt; có video, source và Spec Kit artifacts để đối chiếu.
