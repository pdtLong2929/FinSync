# FinSync — Project Context & Guidelines

> File này cung cấp toàn bộ ngữ cảnh (context), quy chuẩn và hướng dẫn làm việc cho AI Assistant (Antigravity/Gemini) trong workspace **FinSync**. File này được tự động đọc khi mở phiên làm việc mới.

---

## 1. Thông Tin Chung Về Dự Án (Project Overview)

- **Môn học:** CS300 - CSC13002: Nhập môn Công nghệ Phần mềm (Introduction to Software Engineering).
- **Học kỳ:** Fall 2026.
- **Tên nhóm:** Group 04.
- **Tên đề tài:** **FinSync** (Ứng dụng Quản lý Tài chính Cá nhân & Chi tiêu Nhóm tích hợp AI).
- **Mục tiêu sản phẩm:** Kết hợp quản lý tài sản cá nhân (tương tự *Money Lover*) với tính năng chia tiền và quản lý quỹ nhóm thông minh (tương tự *Splitwise*), tích hợp trợ lý tài chính AI cố vấn chi tiêu.
- **Nguyên tắc nghiệp vụ cốt lõi:** Hệ thống thuần túy đóng vai trò **ghi chép thủ công (bookkeeping), tính toán nợ và nhắc nhở** — tuyệt đối **không** kết nối trực tiếp tài khoản ngân hàng thật và **không** tự động trừ tiền người dùng.

### 1.1. Giới thiệu & Giá trị sản phẩm (Introduction & Value Proposition)

- **Giới thiệu:** FinSync là giải pháp quản lý tài chính toàn diện, kết hợp giữa quản lý tài sản cá nhân truyền thống (như Money Lover) và tính năng chia tiền nhóm thông minh (như Splitwise). Ứng dụng được thiết kế cho cá nhân, sinh viên và các nhóm người dùng cần theo dõi chi tiêu chung (tiền phòng trọ, chuyến đi du lịch, sự kiện). Bằng cách tích hợp trợ lý AI thông minh, hệ thống tự động phân tích lịch sử chi tiêu để đề xuất hướng chi tiêu hợp lý cho tháng tiếp theo và tối ưu hóa việc thanh toán nợ nhóm.
- **Giá trị mang lại:** Các ứng dụng quản lý tài chính cá nhân hiện nay thiếu khả năng quản lý chi tiêu nhóm, trong khi các ứng dụng chia tiền nhóm lại không có các tính năng ngân sách và tài khoản cá nhân. Ứng dụng này giải quyết triệt để các vấn đề trên bằng cách hợp nhất tài chính cá nhân và nhóm vào một hệ sinh thái duy nhất, kết hợp công cụ AI để phân tích lịch sử chi tiêu, tự động hóa tính toán nợ và đề xuất hướng chi tiêu tối ưu cho người dùng.

---

## 2. Đối Tượng Người Dùng & Phân Vai (Target Users & Team Members)

### 2.1. Đối tượng người dùng (Target Users)

Hệ thống có **2 Actor chính**:

1. **Regular User (Người dùng cuối):** Cá nhân (sinh viên, người đi làm trẻ) có nhu cầu theo dõi dòng tiền cá nhân, thiết lập mục tiêu tiết kiệm, đồng thời tạo hoặc tham gia các nhóm chi tiêu chung để chia sẻ hóa đơn với bạn bè, đồng nghiệp. Trong nhóm chi tiêu, người dùng có hai vai trò: **Trưởng nhóm (Owner)** — người tạo và quản lý nhóm, và **Thành viên (Member)** — người tham gia nhóm.
2. **Administrator (Quản trị viên):** Người quản lý hệ thống, truy cập qua giao diện web đơn giản để thực hiện các tác vụ quản trị cơ bản: xem danh sách người dùng, khóa/mở khóa tài khoản, quản lý danh mục thu chi mặc định của hệ thống và xem thống kê tổng quan.

### 2.2. Thành viên nhóm phát triển (Team Members)

> *Tất cả thành viên đều tham gia với tư cách Full-stack Engineer trong toàn bộ vòng đời dự án. Vai trò dưới đây nhằm xác định người chịu trách nhiệm chính (Lead).*

| # | MSSV | Họ và Tên | Email | Vai trò chính trong nhóm | Trách nhiệm chính |
|---|----------|----------------------|---------------------------|---------------------------------|-------------------|
| 1 | 24120087 | **Phạm Đình Tiểu Long** | phamlongkh2006@gmail.com | **Group Leader** / Project Manager | Điều phối dự án, chủ trì họp Scrum, quản lý Jira, tổng hợp báo cáo |
| 2 | 24120403 | **Nguyễn Lê Đức Nhật** | nldnhat182006@gmail.com | UI/UX Designer & Frontend Lead | Thiết kế giao diện (Figma), lead phát triển Flutter mobile app |
| 3 | 24120051 | **Ngô Thái Hòa** | ngothaihoa235@gmail.com | Backend Lead | Kiến trúc hệ thống, xây dựng API Spring Boot, thiết kế Database |
| 4 | 24120342 | **Vương Đắc Gia Khiêm** | vuongkhiemvl10@gmail.com | QA Lead & DevOps | Quản lý quy trình kiểm thử (Test Plan/Cases), CI/CD, deployment |
| 5 | 24120038 | **Nguyễn Phú Đạt** | nguyennphuudatt@gmail.com | AI Feature Lead & Documentation | Tích hợp AI (Gemini/OpenAI API), quản lý tài liệu kỹ thuật |

---

## 3. Kiến Trúc & Công Nghệ Sử Dụng (Tech Stack)

- **Mobile Client (Regular User):** Flutter (Dart) — ứng dụng Android native/cross-platform. Quản lý trạng thái bằng BLoC / Riverpod, HTTP client bằng Dio.
- **Web Admin Panel (Administrator):** React.js — quản trị người dùng, quản lý danh mục thu/chi mặc định, xem thống kê hệ thống.
- **Backend API Server:** Spring Boot (Java), kiến trúc RESTful API, bảo mật Spring Security + JWT. API Documentation bằng Swagger / SpringDoc OpenAPI.
- **Cơ sở dữ liệu (Database):** PostgreSQL (Cloud Server), ORM sử dụng Spring Data JPA / Hibernate.
- **Tích hợp Trí tuệ Nhân tạo (AI Feature):** Gọi Google Gemini API / OpenAI API từ Backend để phân tích lịch sử giao dịch và sinh đề xuất kế hoạch chi tiêu tối ưu cho người dùng.
- **Quản lý phiên bản & Quản trị:** Git + GitHub (Repository ở chế độ **Private**), Jira (quản lý task theo Scrum).
- **Môi trường hoạt động:** Ứng dụng di động Android (Flutter) cho Regular User + trang quản trị web (React.js) cho Administrator. Cả hai kết nối với máy chủ Backend chung qua RESTful API.

---

## 4. Phân Vùng Nghiệp Vụ Cốt Lõi (Core Business Domains)

### 4.1. Quản lý Chi tiêu Cá nhân (Personal Expense Management)

- **Mục đích:** Phục vụ độc lập cho một cá nhân nhằm theo dõi toàn bộ tài sản ròng, nguồn tiền riêng và kiểm soát thói quen tiêu xài của bản thân.
- **Đặc điểm kỹ thuật & nghiệp vụ:**
  - Dữ liệu hoàn toàn riêng tư, chỉ chủ tài khoản mới có quyền truy cập.
  - Quản lý các tài khoản nguồn vốn riêng biệt như ví tiền mặt, tài khoản ngân hàng cá nhân, thẻ tín dụng cá nhân.
  - Thiết lập hạn mức ngân sách (Budget) cho các danh mục cá nhân (như tiền nhà, học phí, mua sắm cá nhân) và thiết lập mục tiêu tiết kiệm dài hạn (Savings Goals).

### 4.2. Quản lý Chi tiêu Nhóm (Group Expense Management)

- **Mục đích:** Phục vụ cho một tập thể (từ 2 người trở lên như nhóm bạn đi du lịch, nhóm bạn ở ghép trọ, nhóm làm đồ án) để giải quyết bài toán "ai trả tiền gì cho ai" và quản lý quỹ chung.
- **Đặc điểm kỹ thuật & nghiệp vụ:**
  - Dữ liệu được chia sẻ công khai giữa các thành viên trong cùng một nhóm được cấp phép.
  - Không dùng để quản lý tài sản cá nhân mà dùng để ghi nhận các khoản chi phát sinh chung của tập thể (ví dụ: tiền thuê phòng trọ hàng tháng, tiền vé xe, tiền ăn chung khi đi chơi).
  - Tích hợp thuật toán tự động chia tiền (chia đều, chia theo tỷ lệ hoặc theo số tiền thực tế từng người ứng trước) và thuật toán tối ưu hóa nợ (giảm thiểu số lần chuyển khoản qua lại giữa các thành viên).

### 4.3. Sự kết hợp đồng điệu trong hệ thống

Khi một thành viên ghi nhận khoản chi chung cho nhóm, hệ thống sẽ tự động tính toán số tiền mỗi người cần trả và gửi thông báo đến các thành viên còn nợ (ví dụ: *"Bạn đang nợ Minh 200.000đ cho khoản ăn tối nhóm"*). Việc thanh toán thực tế diễn ra ngoài ứng dụng (chuyển khoản ngân hàng, tiền mặt…), sau đó người trả xác nhận đã thanh toán trên app.

> **Lưu ý quan trọng:** Toàn bộ hệ thống chỉ đóng vai trò ghi sổ và nhắc nhở, tuyệt đối không tự động trừ tiền hay tác động đến tài khoản ngân hàng thật của người dùng.

---

## 5. Các Nhóm Tính Năng Chính (Scope & Key Features)

**10 Nhóm chức năng (Functional Groups):**

1. **Authentication & Security:** Module xác thực xử lý việc đăng ký tài khoản, đăng nhập, đăng xuất và khôi phục mật khẩu thông qua mã token mã hóa (JWT). Đảm bảo dữ liệu tài chính cá nhân và nhóm của người dùng luôn riêng tư, an toàn.
2. **Profile Management:** Cho phép người dùng xem và chỉnh sửa thông tin cá nhân, thay đổi mật khẩu, cập nhật ảnh đại diện và cấu hình đơn vị tiền tệ mặc định (VND, USD).
3. **Wallet & Personal Account Management:** Cho phép người dùng tạo và quản lý các ví ghi sổ (tiền mặt, tài khoản ngân hàng, thẻ tín dụng) để theo dõi số dư. Các ví chỉ mang tính chất nhập liệu thủ công. Hệ thống tổng hợp tất cả số dư vào bảng điều khiển duy nhất để cung cấp cái nhìn tổng quan về tài sản ròng.
4. **Personal Transaction Tracking:** Người dùng ghi chép nhanh chóng các khoản thu, chi cá nhân bằng cách chỉ định số tiền, danh mục, ghi chú, ngày tháng và hình ảnh hóa đơn kèm theo.
5. **Group Management & Shared Wallets:** Cho phép người dùng tạo nhóm (phòng trọ, đi chơi, dự án), mời thành viên tham gia qua liên kết hoặc mã code, phân quyền Owner và Member. Mỗi nhóm có ví chung tách biệt hoàn toàn với tài chính cá nhân.
6. **Smart Debt Split & Settlement:** Tự động phân chia các khoản chi tiêu chung (chia đều, theo tỷ lệ phần trăm hoặc số tiền cụ thể). Hiển thị bảng tổng hợp ai đang nợ ai bao nhiêu, gửi thông báo nhắc nhở thanh toán, cho phép đánh dấu "đã thanh toán" khi hoàn tất ngoài ứng dụng.
7. **Budget Planning & Alerting:** Cho phép thiết lập hạn mức chi tiêu cho từng danh mục theo tuần/tháng. Kích hoạt cảnh báo trực quan khi chi tiêu tiệm cận hoặc vượt quá các ngưỡng giới hạn (80% và 100%).
8. **Financial Reporting & Analytics:** Trực quan hóa cấu trúc dòng tiền qua biểu đồ tương tác (biểu đồ tròn, biểu đồ cột). Tổng kết báo cáo chi tiêu nhóm sau chuyến đi, hỗ trợ xuất file PDF hoặc Excel.
9. **Savings Goal Management:** Giúp người dùng thiết lập các mục tiêu tài chính dài hạn (mua sắm, du lịch), theo dõi tiến độ hoàn thành.
10. **System Administration (Admin Panel):** Quản lý danh sách người dùng (khóa/mở khóa), danh mục mặc định, thống kê tổng quan.

**Tính năng AI nổi bật (Standalone AI Feature):**

- **AI Financial Advisor:** Vào đầu mỗi tháng (hoặc theo yêu cầu), AI tự động quét toàn bộ lịch sử giao dịch của tháng trước — bao gồm các khoản thu, chi cá nhân và chi tiêu nhóm — để phân tích cấu trúc dòng tiền, phát hiện các danh mục chi tiêu bất thường hoặc tăng đột biến. Cụ thể, hệ thống sẽ:
  1. So sánh chi tiêu thực tế từng danh mục với ngân sách đã thiết lập và trung bình các tháng trước.
  2. Cảnh báo các danh mục có xu hướng vượt ngân sách hoặc tăng bất thường (ví dụ: *"Chi phí ăn uống tháng trước tăng 40% so với trung bình 3 tháng, bạn nên giới hạn ở mức 2.5 triệu tháng này"*).
  3. Đề xuất phân bổ ngân sách tối ưu cho từng danh mục dựa trên thu nhập, mục tiêu tiết kiệm và thói quen chi tiêu thực tế.
  4. Đánh giá tiến độ đạt mục tiêu tiết kiệm và đưa ra lời khuyên điều chỉnh nếu cần.
- **Giá trị thực tiễn:** Chuyển đổi dữ liệu chi tiêu thô thành những định hướng tài chính chủ động, giúp người dùng không chỉ biết mình đã tiêu gì mà còn biết nên tiêu như thế nào trong tháng tới.

---

## 6. Quy Định Làm Việc & Báo Cáo Môn Học (Guidelines & Rules)

### 6.1. Định dạng tài liệu & Báo cáo
- Toàn bộ tài liệu báo cáo phải viết bằng **tiếng Anh** bằng định dạng **Markdown (`.md`)**.
- Các sơ đồ phải ưu tiên vẽ bằng cú pháp **Mermaid syntax**.
- **Bắt buộc có dòng phân công (Attribution Line)** ngay dưới mỗi tiêu đề section:
  ```markdown
  > *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*
  ```
- Nộp bài kèm cả file gốc `.md` và file export `.pdf`, nén zip theo chuẩn: `PA1-Group[GroupId].zip`.

### 6.2. Quy trình Agile / Scrum (theo WeeklyReport.md)
- Mỗi bài tập PA là **1 Sprint** (cố định 2-3 tuần).
- Mỗi Sprint tổ chức **4 cuộc họp**:
  1. **1 Sprint Planning:** Đầu sprint, bóc tách requirement, tạo và gán task Jira.
  2. **2 Weekly Scrum Meetings:** Giữa sprint, kiểm tra tiến độ, mỗi thành viên trả lời 3 câu hỏi:
     - *What have I done since last week?*
     - *What will I do until next week?*
     - *What issues / problems / obstacles do I have?*
  3. **1 Sprint Review / Retrospective:** Cuối sprint, đánh giá kết quả, trả lời 5 câu hỏi hồi tưởng (What went well, What went wrong, Root causes, Actionable improvements, Lessons learned).
- Tất cả biên bản họp lưu tại `/docs/management/weekly-reports/` hoặc trong báo cáo.

### 6.3. Quy tắc quản lý công việc trên Jira
- **Mọi công việc** (viết report, học công nghệ, thiết kế, code, test) đều phải có task trên Jira.
- Mỗi task được gán cho **chính xác 1 thành viên** (không gán chung).
- Task phải có đủ: Ngày tạo, ngày gán, ngày hoàn thành.
- **Tuyệt đối không:** Tạo task, gán và Done cùng lúc sau khi đã làm xong. Task phải tạo trước khi bắt đầu.
- Chụp ảnh màn hình Jira board đưa vào báo cáo tuần.

### 6.4. Quy tắc Git & Bảo mật
- Repository ở chế độ **Private**.
- **Tuyệt đối không commit API keys, mật khẩu, JWT secret hay file `.env` lên GitHub.**
- Cấu trúc thư mục chuẩn:
  ```text
  FinSync/
  ├── src/                          # Source code (mobile, backend, admin-web)
  ├── docs/                         # Documentation
  │   ├── management/               # weekly-reports, meeting-notes
  │   ├── requirements/             # vision, use cases, spec
  │   ├── analysis-and-design/      # architecture, diagrams, UI design
  │   └── test/                     # test plan, test cases
  ├── screenshots/                  # Ảnh chụp ứng dụng, Jira, Git log
  └── GEMINI.md                     # File context cho AI Assistant
  ```

---

## 7. Hướng Dẫn Dành Cho AI Assistant

1. **Hiểu rõ vai trò:** Hỗ trợ nhóm thực hiện đúng các tiêu chuẩn công nghệ phần mềm của HCMUS, tuân thủ đúng định dạng của giáo viên và trợ giảng (TAs).
2. **Bảo toàn dữ liệu:** Khi người dùng yêu cầu chỉnh sửa định dạng hoặc cấu trúc tài liệu, **luôn bảo toàn tối đa nội dung nghiệp vụ** đã có của nhóm, không tự ý rút ngắn hoặc xóa bỏ trừ khi có yêu cầu cụ thể.
3. **Tuân thủ cấu trúc tài liệu:** Luôn đối chiếu với các file đặc tả (`pa1_2026_project_assignment_specification.md`, `WeeklyReport.md`, v.v.) trước khi đề xuất thay đổi.
4. **Phân biệt 2 phân vùng nghiệp vụ:** Luôn phân biệt rõ giữa "Quản lý Chi tiêu Cá nhân" (dữ liệu riêng tư, ví cá nhân) và "Quản lý Chi tiêu Nhóm" (dữ liệu chia sẻ, quỹ chung) khi đề xuất thiết kế hoặc code.
5. **Nguyên tắc bảo mật:** Không bao giờ đề xuất hardcode API keys, passwords, hoặc secrets trong source code. Luôn sử dụng environment variables.
