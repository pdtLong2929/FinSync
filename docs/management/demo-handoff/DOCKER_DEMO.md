# Chạy Backend demo bằng Docker Compose
> *Thực hiện: Nhật (AI hỗ trợ) | Người dùng: Hòa, Đạt, Khiêm | Rà soát: Chờ nhóm xác nhận*

## 1. Chạy bằng một lệnh
> *Thực hiện: Nhật | Rà soát: Chờ xác nhận*

Cài Docker Desktop, bật Docker và dùng Linux containers cùng Docker Compose v2. Mở terminal tại thư mục gốc repository, nơi có `docker-compose.yml`, rồi chạy:

```powershell
docker compose up --build -d --wait --wait-timeout 240
```

Không cần cài Java, Maven, PostgreSQL, Redis trên máy; không cần tạo `.env` hay tự nhập secret. Lần đầu cần Internet và thời gian tải image/dependency. Lệnh chờ tới khi dịch vụ sẵn sàng, không chỉ vừa tạo container.

Compose tự thực hiện:

1. `init-secrets` sinh password database và JWT secret ngẫu nhiên nếu chưa có, lưu trong volume riêng. Chạy lại không thay secret.
2. PostgreSQL khởi tạo database `FinSync`; Redis khởi động với lưu trữ AOF.
3. Docker build Backend bằng Java 21/Maven; Flyway tạo schema; Hibernate kiểm tra mapping.
4. Backend chạy bằng user không phải root; healthcheck kiểm tra trạng thái ứng dụng, PostgreSQL và Redis.

`init-secrets` hiện trạng thái `Exited (0)` là bình thường vì đây là tác vụ khởi tạo một lần. Các dịch vụ còn lại cần ở trạng thái healthy.

## 2. Kiểm tra và kết nối Android
> *Thực hiện: Nhật | Phối hợp: Đạt, Khiêm | Rà soát: Chờ xác nhận*

```powershell
docker compose ps -a
Invoke-RestMethod http://localhost:8080/actuator/health
```

Kết quả health cần có `"status":"UP"`; Actuator có thể kèm tên nhóm `liveness`/`readiness`. Endpoint không hiển thị thông tin cấu hình hoặc chi tiết thành phần. Chưa triển khai Swagger UI; xem [hợp đồng API](ACCOUNT_API.md) để gọi 6 endpoint tài khoản.

| Nơi gọi API | Địa chỉ |
|---|---|
| Postman trên máy chạy Docker | `http://localhost:8080/api/v1/` |
| Android Emulator chuẩn trên cùng máy | `http://10.0.2.2:8080/api/v1/` |
| Điện thoại thật hoặc máy khác trong LAN | `http://<IP-LAN-máy-chạy-Docker>:8080/api/v1/`, sau khi mở bind như bên dưới. |

Mặc định chỉ mở API trên localhost. Để cho thành viên khác gọi qua LAN, tạo `.env` tại thư mục gốc (có thể sao chép [.env.example](../../../.env.example)):

```dotenv
API_BIND_ADDRESS=0.0.0.0
API_PORT=8080
```

Chạy lại lệnh Compose và cho phép cổng tương ứng trên firewall trong mạng demo. HTTP dành cho demo nội bộ; triển khai công khai cần HTTPS. PostgreSQL/Redis không công khai cổng host, nên không xung đột với database cài trên máy.

Nếu cổng 8080 đã bận, đặt `API_PORT=8081` rồi chạy lại; đổi URL FE/Postman sang cổng 8081. `.env` ở gốc chỉ cần cho các tùy chọn này, không cần đưa DB password/JWT secret vào đó.

## 3. Dừng, chạy lại và cập nhật code
> *Thực hiện: Nhật | Rà soát: Chờ xác nhận*

```powershell
# Xem lỗi khởi động nếu có
docker compose logs --tail 100 backend postgres redis init-secrets

# Dừng và gỡ container/network, giữ tài khoản và secret trong volumes
docker compose down

# Sau khi pull code, build và chạy lại
docker compose up --build -d --wait --wait-timeout 240
```

Giữ cùng thư mục/tên project Compose để dùng lại volumes. **Không thêm `-v` vào `down` nếu muốn giữ dữ liệu**: tùy chọn này xóa cả database, Redis và secret. Không xóa riêng volume secret khi vẫn giữ database vì password được sinh lại sẽ không khớp database cũ. Không tự xóa dữ liệu để xử lý lỗi; hỏi Nhật nếu cần đối chiếu schema.

Nếu Redis mất dữ liệu blacklist, cần phối hợp Nhật thu hồi các phiên hiện có trước khi mở lại xác thực; không coi Redis trống là mọi token đều an toàn.

## 4. Khi máy đã chạy cấu hình Docker cũ
> *Thực hiện: Nhật | Rà soát: Chờ xác nhận*

Stack này dùng volumes `demo_postgres_data`, `demo_redis_data`, `demo_secrets`, tách khỏi `postgres_data`/`redis_data` của cấu hình trước. **Không tự chuyển tài khoản từ database cũ sang stack mới**; lần đầu chạy stack mới là database demo mới, phải đăng ký tài khoản mới.

Dữ liệu cũ không bị xóa bởi thay đổi này. Muốn chuyển dữ liệu thì phối hợp Nhật để backup/import có kiểm tra; không gắn volume cũ với password mới. Nếu instance cũ hoặc BE chạy bằng IDE chiếm 8080, đổi `API_PORT` thay vì tự dừng dịch vụ người khác đang dùng.

## 5. Docker và chạy Java trực tiếp khác nhau thế nào?
> *Thực hiện: Nhật | Rà soát: Chờ xác nhận*

Compose dùng service `init-secrets`, mount secret chỉ đọc và Spring configtree; không copy file `.env` vào image. `.dockerignore` chỉ cho phép Docker nhận `pom.xml` và `src/`, loại các secret, build output và tài liệu ngoài phạm vi build.

Nếu Nhật chạy Java ngoài Docker để debug, vẫn tự cấu hình DB/Redis/secret theo [src/backend/.env.example](../../../src/backend/.env.example). Cấu hình ứng dụng hiện có hỗ trợ đọc `.env` trong working directory qua `spring.config.import`; cách này khác `.env` của Compose ở thư mục gốc.

Tham khảo: [thứ tự khởi động Docker Compose](https://docs.docker.com/compose/how-tos/startup-order/) và [health endpoint Spring Boot](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html).

## 6. Kết quả kiểm chứng Docker
> *Thực hiện: Nhật (AI hỗ trợ) | Ngày: 05/11/2026 | Rà soát nhóm: Chưa xác nhận*

Đã build image từ source và chạy stack riêng `finsync-compose-check` trên cổng 58080 với volumes thử nghiệm mới. Không dùng database demo hiện có của nhóm.

- Backend, PostgreSQL, Redis đều healthy; tác vụ khởi tạo secret kết thúc với mã 0.
- Gọi HTTP thật thành công: đăng ký, login, xem/sửa hồ sơ, refresh, logout; API bảo vệ chặn request thiếu token và phiên đã thu hồi.
- Redis có key blacklist sau logout; refresh token cũ bị từ chối sau khi xoay.
- Sau `down` rồi `up` giữ volumes: hồ sơ đã sửa vẫn còn, JWT đang hợp lệ vẫn truy cập được, blacklist và phiên đã thu hồi vẫn được giữ.
- Backend chạy UID 10001 và không có file `/app/.env` trong image.

Đây là kiểm chứng stack Docker và API; chưa phải kết quả nối UI Android. Stack và volumes thử nghiệm được dọn sau kiểm tra, không ảnh hưởng dữ liệu thật của nhóm.
