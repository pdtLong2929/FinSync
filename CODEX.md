# FinSync — Project Context & Guidelines

> File này cung cấp ngữ cảnh, quy chuẩn và hướng dẫn làm việc cho AI Assistant (Codex/Gemini) trong workspace **FinSync**. Khi bắt đầu phiên mới, đọc file này và các tài liệu liên quan được dẫn bên dưới; không giả định công cụ luôn tự động nạp file.

## 0. Trạng thái hiện tại và tài liệu cần đọc

**Cập nhật ngữ cảnh: 04/11/2026.** Phân biệt rõ yêu cầu đồ án, prototype đang có và thiết kế đề xuất.

| Nguồn | Vai trò |
|---|---|
| [database_design.md](database_design.md) | Thiết kế database **Final v1.0**, tiếng Việt, **22 bảng và 6 sơ đồ Mermaid**; có đầy đủ field, kiểu PostgreSQL, NULL/default, FK, ràng buộc, index, mapping JPA và thứ tự migration. Là tài liệu bàn giao BE, chưa phải migration/entity đã triển khai hay kết quả nhóm đã nghiệm thu. |
| [FE/src/model.ts](FE/src/model.ts), [FE/src/workspace.tsx](FE/src/workspace.tsx) | Kiểu dữ liệu, ví, giao dịch, ngân sách, thành viên, chia tiền và trả nợ của prototype Figma Make. |
| [FE/src/App.tsx](FE/src/App.tsx), [FE/src/WalletScreens.tsx](FE/src/WalletScreens.tsx), [FE/src/GroupScreens.tsx](FE/src/GroupScreens.tsx) | Màn hình và hành vi hiện có; ưu tiên source này khi khác tài liệu handoff cũ. Đọc [FE/AGENTS.md](FE/AGENTS.md) khi làm việc trong FE. |
| [PA/PA3/use-cases/client-use-cases.md](PA/PA3/use-cases/client-use-cases.md) | 12 ca sử dụng phía người dùng; không đồng nghĩa với 12 nhóm chức năng của toàn đồ án. |
| [PA/PA3/use-cases/admin-account-use-cases.md](PA/PA3/use-cases/admin-account-use-cases.md), [PA/PA3/use-cases/uc-ai-features.md](PA/PA3/use-cases/uc-ai-features.md) | Yêu cầu tài khoản, admin web và AI cần giữ dù source FE chưa triển khai. |

- **FE hiện tại:** Prototype React + TypeScript + Vite + Tailwind CSS xuất từ Figma Make; giao diện mô phỏng mobile với bốn tab Overview, Wallets, Groups, Reports. Dữ liệu nằm trong bộ nhớ React, tải lại sẽ khởi tạo lại; chưa chứng minh tích hợp backend, PostgreSQL, JWT, AI hoặc admin web.
- **Phạm vi toàn đồ án:** Vẫn gồm 10 nhóm chức năng và AI Financial Advisor. Không bỏ chức năng chỉ vì chưa có trong prototype; cũng không ghi nhận tính năng dự kiến là đã triển khai.
- **Database đã đối chiếu FE:** Gộp bảng `group_expense_payments` vào `group_expenses.paid_by_member_id`. V1.0 chốt một người ứng toàn bộ hóa đơn hoặc trả hoàn toàn bằng quỹ; chưa hỗ trợ nhiều người cùng ứng hoặc trộn nguồn.
- **Các quyết định quan trọng:** Ngân sách hiện tính chi cá nhân; chi nhóm không tự tạo giao dịch cá nhân. Chia tiền hỗ trợ đều/số tiền/phần trăm/trọng số; trả nợ hỗ trợ toàn bộ hoặc một phần. Hoàn tác xóa giao dịch có cửa sổ 7 giây trên FE. Thành viên nhập tên chưa có tài khoản được mô hình hóa bằng `group_members.user_id` tùy chọn, không mặc nhiên có quyền truy cập nhóm.
- **Các lựa chọn đã chốt cho thiết kế v1.0:** Guest liên kết tài khoản bằng lời mời chỉ định; Owner quản lý quỹ/hoàn quỹ trong số dư; không quy đổi VND/USD; tiết kiệm là dành riêng tiền trong ví; trả nợ không cần bước người nhận duyệt riêng. Hóa đơn nhóm/trả nợ/chuyển ví đã ghi là sổ chỉ thêm. Thay đổi những lựa chọn này cần cập nhật thiết kế và migration, không coi chúng là hành vi đã triển khai trong FE.
- **Liên kết dòng tiền cá nhân/nhóm:** personal_transactions có scope PERSONAL/GROUP_MOVEMENT, category_id tùy scope và các FK tùy chọn group_expense_id/settlement_id/group_fund_entry_id. Người dùng chủ động ghi vào ví; GROUP_MOVEMENT ảnh hưởng số dư nhưng bị loại khỏi thu/chi báo cáo, ngân sách và phần chi cá nhân của AI. Backend kiểm tra tối đa một dòng còn hiệu lực/user/nguồn; không tự nhận diện giao dịch bị nhập nhầm PERSONAL.
- **Hợp đồng entity:** Mỗi bảng có id UUID, created_at, updated_at, version cộng các cột riêng trong tài liệu. Tiền dùng BigDecimal/numeric(19,2), enum varchar + CHECK, mọi FK RESTRICT. Các luật tổng nhiều dòng/cùng chủ/cùng nhóm do service transaction và khóa thực thi; @Version không thay thế kiểm tra đồng thời.
- **Nguồn và kiểm chứng:** Đã đọc source FE để điều chỉnh thiết kế, chưa chạy kiểm thử ứng dụng trong lần đối chiếu này. Các báo cáo kiểm thử/ảnh trong FE là bằng chứng đi kèm, không phải kết quả vừa chạy lại. `database_design.pdf` là bản xuất riêng, không tự đồng bộ với Markdown; cần xuất lại khi muốn phát hành bản cập nhật.

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

| # | MSSV | Họ và Tên | Email | Vai trò chính trong nhóm | Trách nhiệm chính (Lead) |
|---|----------|----------------------|---------------------------|---------------------------------|-------------------|
| 1 | 24120087 | **Phạm Định Tiểu Long** | phamlongkh2006@gmail.com | **Project Manager / Group Leader** | Quản lý tiến độ dự án, điều phối công việc, chủ trì họp Sprint, quản lý Jira board |
| 2 | 24120038 | **Nguyễn Phú Đạt** | nguyennphuudatt@gmail.com | **UI/UX Designer & Frontend Lead** | Lead thiết kế UI/UX (wireframes/mockups), đảm bảo tính nhất quán giao diện, lead phát triển client app |
| 3 | 24120403 | **Nguyễn Lê Đức Nhật** | nldnhat182006@gmail.com | **Backend Lead** | Lead thiết kế kiến trúc backend, xây dựng API Spring Boot, thiết kế cơ sở dữ liệu |
| 4 | 24120342 | **Vương Đắc Gia Khiêm** | vuongkhiemvl10@gmail.com | **QA Lead & DevOps** | Lead chiến lược kiểm thử, viết test cases, quản lý CI/CD pipeline, triển khai hệ thống |
| 5 | 24120051 | **Ngô Thái Hòa** | ngothaihoa235@gmail.com | **AI Feature Lead & Documentation** | Lead triển khai tính năng AI (Gemini/OpenAI integration), quản lý tài liệu kỹ thuật |
---

## 3. Kiến Trúc & Công Nghệ Sử Dụng (Tech Stack)

> Các thành phần bên dưới là kiến trúc sản phẩm dự kiến. Source `FE/` hiện tại là prototype web cho người dùng cuối, không phải admin web và không xác nhận mobile/backend đã hoàn thành.

- **Mobile Client (Regular User):** 
  - Android Client: Kotlin / Jetpack Compose (tuân thủ Kotlin style guide, UI reactive với Composables) hoặc Flutter (Dart).
  - Tích hợp gọi API Backend qua HTTP Client với interceptor tự động gắn JWT Token.
- **Backend API Server:** Spring Boot (Java 21), RESTful API phân tầng, Spring Security + JWT access token + refresh token xoay vòng, Flyway migration. Refresh token lưu hash trong PostgreSQL auth_sessions; blacklist access token theo jti trong Redis có TTL. Logout/khóa tài khoản thu hồi phiên, API kiểm user/session và blacklist; chi tiết tại mục 5.3 của database_design.md. Đây là thiết kế BE, chưa triển khai trong FE. API Documentation bằng Swagger / SpringDoc OpenAPI.
- **AI Microservice & Data Processing:** Python (FastAPI) tích hợp Google Gemini API / OpenAI API để xử lý thuật toán phân tích chi tiêu, Notification Regex parsing algorithms và sinh kế hoạch tài chính tối ưu.
- **Web Admin Panel (Administrator):** React.js — quản trị người dùng, quản lý danh mục thu/chi mặc định, xem thống kê hệ thống.
- **Cơ sở dữ liệu (Database):** PostgreSQL (Cloud/Docker), ORM sử dụng Spring Data JPA / Hibernate.
- **DevOps & Quản trị dự án:** Docker & Docker Compose, Git + GitHub (Repository ở chế độ **Private**), Jira (quản lý task theo chuẩn Agile/Scrum).
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
  - Chia chi phí đều, theo số tiền cụ thể, phần trăm hoặc trọng số; phân biệt phần chi phí phải chịu với tiền ứng trước. FE hiện có một người ứng toàn bộ hóa đơn; công nợ được tính từ hóa đơn, phần chia và lịch sử trả nợ để đề xuất bù trừ giữa thành viên.

### 4.3. Sự kết hợp đồng điệu trong hệ thống

Khi một thành viên ghi nhận khoản chi chung cho nhóm, hệ thống sẽ tự động tính toán số tiền mỗi người cần trả và gửi thông báo đến các thành viên còn nợ (ví dụ: *"Bạn đang nợ Minh 200.000đ cho khoản ăn tối nhóm"*). Việc thanh toán thực tế diễn ra ngoài ứng dụng (chuyển khoản ngân hàng, tiền mặt…), sau đó người trả xác nhận đã thanh toán trên app.

> **Lưu ý quan trọng:** Toàn bộ hệ thống chỉ đóng vai trò ghi sổ và nhắc nhở, tuyệt đối không tự động trừ tiền hay tác động đến tài khoản ngân hàng thật của người dùng.

---

## 5. Các Nhóm Tính Năng Chính (Scope & Key Features)

**10 Nhóm chức năng (Functional Groups):**

Đây là phạm vi yêu cầu sản phẩm, không phải danh sách tính năng đã hoàn thành trong FE.

1. **Authentication & Security:** Đăng ký, đăng nhập, đăng xuất, thu hồi mọi phiên và khôi phục mật khẩu. Dùng JWT access token ký RS256, refresh token ngẫu nhiên lưu hash/xoay mỗi lần refresh, blacklist Redis theo jti và phiên PostgreSQL theo sid. JWT được ký, không phải mã hóa nội dung. Blacklist không thêm bảng PostgreSQL; thiết kế vẫn có 22 bảng. Token đặt lại mật khẩu dùng một lần là luồng riêng.
2. **Profile Management:** Cho phép người dùng xem và chỉnh sửa thông tin cá nhân, thay đổi mật khẩu, cập nhật ảnh đại diện và cấu hình đơn vị tiền tệ mặc định (VND, USD).
3. **Wallet & Personal Account Management:** Cho phép người dùng tạo và quản lý các ví ghi sổ (tiền mặt, tài khoản ngân hàng, thẻ tín dụng) để theo dõi số dư. Các ví chỉ mang tính chất nhập liệu thủ công. Hệ thống tổng hợp tất cả số dư vào bảng điều khiển duy nhất để cung cấp cái nhìn tổng quan về tài sản ròng.
4. **Personal Transaction Tracking:** Người dùng ghi chép nhanh chóng các khoản thu, chi cá nhân bằng cách chỉ định số tiền, danh mục, ghi chú, ngày tháng.
5. **Group Management & Shared Wallets:** Cho phép người dùng tạo nhóm (phòng trọ, đi chơi, dự án), mời thành viên tham gia qua liên kết hoặc mã code, phân quyền Owner và Member. Mỗi nhóm có ví chung tách biệt hoàn toàn với tài chính cá nhân.
6. **Smart Debt Split & Settlement:** Chia chi tiêu chung theo đều, tỷ lệ phần trăm, số tiền cụ thể hoặc trọng số. Hiển thị ai đang nợ ai, ghi nhận trả nợ toàn bộ/một phần sau khi thanh toán ngoài ứng dụng. Nhắc nợ qua thông báo là yêu cầu sản phẩm chưa được FE triển khai.
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
- **Ranh giới dữ liệu AI:** Chỉ tính phần chi nhóm mà người dùng phải chịu, không tính toàn bộ hóa đơn, tiền ứng hoặc tiền trả nợ như chi tiêu mới. Chỉ gửi dữ liệu tổng hợp được phép đến nhà cung cấp; lưu một kết quả/người dùng/tháng, hỗ trợ phân tích cơ bản khi AI lỗi. Áp dụng tư vấn cần người dùng xác nhận. Ngân sách hiện đối chiếu chi cá nhân, nên phần phân tích nhóm phải tách rõ. Chatbot hội thoại chưa thuộc phạm vi hiện tại.

---

## 6. Quy Định Làm Việc & Báo Cáo Môn Học (Guidelines & Rules)

### 6.1. Định dạng tài liệu & Báo cáo
- Toàn bộ tài liệu báo cáo phải viết bằng **tiếng Anh** bằng định dạng **Markdown (`.md`)**.
- **Ngoại lệ theo yêu cầu người dùng:** `database_design.md` viết bằng **tiếng Việt**, giữ tên bảng/trường bằng tiếng Anh và Mermaid chia theo tính năng. Người dùng đã yêu cầu bản **Final v1.0** với đầy đủ field và quan hệ để bàn giao BE; tiếp tục duy trì độ chi tiết này, không tự dịch sang tiếng Anh hoặc rút về bản nháp.
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
- Các đường dẫn hiện có cần dùng để tìm tài liệu/source:
  ```text
  FinSync/
  ├── CODEX.md                      # Ngữ cảnh dự án cho AI
  ├── database_design.md            # Thiết kế database Final v1.0 cho BE
  ├── database_design.pdf           # Bản xuất, cần kiểm tra độ cập nhật
  ├── FE/                          # Source prototype Figma Make
  │   ├── src/                     # React/TypeScript và logic trong bộ nhớ
  │   ├── docs/                    # Handoff, báo cáo prototype
  │   ├── scripts/                 # Script kiểm thử và xuất minh chứng
  │   └── public/                  # Tài nguyên và ảnh minh chứng
  ├── PA/                          # Đặc tả, báo cáo PA1/PA2/PA3
  ├── WeeklyReport.md
  └── README.md
  ```
- Cấu trúc `src/` và `docs/management`, `docs/requirements`, `docs/analysis-and-design`, `docs/test` là định hướng tổ chức khi triển khai sản phẩm; không giả định các thư mục đó đã tồn tại hoặc tự di chuyển file hiện tại.

### 6.5. Quy chuẩn Commit Message (Conventional Commits)
- Bắt buộc tuân theo định dạng: `<type>(<scope>): <subject>`
  - `feat`: Tính năng mới (ví dụ: `feat(auth): implement JWT token generation`)
  - `fix`: Sửa lỗi (ví dụ: `fix(split): resolve decimal rounding error in debt calculation`)
  - `docs`: Tài liệu, báo cáo (ví dụ: `docs(api): add swagger annotations for wallet endpoints`)
  - `refactor`: Tái cấu trúc mã nguồn không làm đổi logic (ví dụ: `refactor(wallet): simplify balance computation`)
  - `test`: Viết unit/integration test (ví dụ: `test(auth): add unit tests for AuthService`)
  - `chore`: Cấu hình build, dependency, gitignore (ví dụ: `chore(deps): upgrade spring-boot to 3.2.0`)
- Tiêu đề commit viết bằng **tiếng Anh**, thể hiện hành động ở thì hiện tại (imperative mood), không viết hoa chữ đầu, không kết thúc bằng dấu chấm.

### 6.6. Quy chuẩn Coding Conventions (Theo Team Contract mục D.4)
- **Android Client (Kotlin):**
  - Tuân thủ [Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).
  - Đặt tên: Biến và hàm dùng lowerCamelCase, Class/Interface và Composables dùng UpperCamelCase (PascalCase).
  - Phân tách rõ UI (Jetpack Compose) và ViewModel/State.
- **Mobile Client (Flutter / Dart - nếu dùng):**
  - Tuân thủ [Effective Dart](https://dart.dev/guides/language/effective-dart).
  - Đặt tên: Class/Widget dùng PascalCase, biến/hàm dùng lowerCamelCase, tên file dùng snake_case.dart.
- **Backend (Java / Spring Boot):**
  - Tuân thủ quy chuẩn đặt tên Java chuẩn: Class/Interface dùng PascalCase, method/variable dùng lowerCamelCase, hằng số dùng UPPER_SNAKE_CASE.
  - Cấu trúc phân tầng chuẩn: controller → service (interface + impl) → repository → model/entity + dto.
  - Tuyệt đối không hardcode SQL, sử dụng Spring Data JPA queries hoặc JPQL.
  - Sử dụng DTO để nhận và trả dữ liệu qua API, không trả trực tiếp Database Entity ra ngoài client.
- **AI Microservice (Python / FastAPI):**
  - Tuân thủ [PEP 8](https://peps.python.org/pep-0008/) style guide.
  - Đặt tên: Biến, hàm và tên file dùng snake_case.py; Class dùng UpperCamelCase (PascalCase).
  - Xử lý các logic thuật toán phức tạp như Notification Regex parsing algorithms, AI data formatting.
- **Web Admin (React.js):**
  - Đặt tên: Component dùng PascalCase.jsx, hooks/helpers dùng camelCase.js.
  - Tách components nhỏ gọn, tái sử dụng, phân tách rõ pages/ và components/.
- **Quy tắc chung:** Mọi mã nguồn phải có comment giải thích rõ ràng cho các đoạn logic phức tạp. Tên biến, hàm, lớp phải có tính mô tả cao và viết hoàn toàn bằng **tiếng Anh**.
---

## 7. Hướng Dẫn Dành Cho AI Assistant

> **Database design (Final v1.0):** Đọc [database_design.md](database_design.md) trước khi tạo entity, migration hoặc service. Đây là thiết kế chi tiết tiếng Việt đã đối chiếu `FE` và giữ phạm vi AI/admin. “Final” là phiên bản bàn giao theo yêu cầu người dùng, không có nghĩa đã triển khai hay nhóm đã duyệt/kiểm thử. Phải đọc cả cột chung, cột riêng và luật service, không chỉ sao chép Mermaid.

1. **Hiểu rõ vai trò:** Hỗ trợ nhóm thực hiện đúng các tiêu chuẩn công nghệ phần mềm của HCMUS, tuân thủ đúng định dạng của giáo viên và trợ giảng (TAs).
2. **Bảo toàn dữ liệu:** Khi người dùng yêu cầu chỉnh sửa định dạng hoặc cấu trúc tài liệu, **luôn bảo toàn tối đa nội dung nghiệp vụ** đã có của nhóm, không tự ý rút ngắn hoặc xóa bỏ trừ khi có yêu cầu cụ thể.
3. **Tuân thủ cấu trúc tài liệu:** Luôn đối chiếu với các file đặc tả (`pa1_2026_project_assignment_specification.md`, `WeeklyReport.md`, v.v.) trước khi đề xuất thay đổi.
4. **Phân biệt 2 phân vùng nghiệp vụ:** Luôn phân biệt rõ giữa "Quản lý Chi tiêu Cá nhân" (dữ liệu riêng tư, ví cá nhân) và "Quản lý Chi tiêu Nhóm" (dữ liệu chia sẻ, quỹ chung) khi đề xuất thiết kế hoặc code.
5. **Nguyên tắc bảo mật:** Không bao giờ đề xuất hardcode API keys, passwords, hoặc secrets trong source code. Luôn sử dụng environment variables.
6. **Đối chiếu đúng nguồn:** Source FE mô tả hành vi prototype; đặc tả mô tả phạm vi sản phẩm; `database_design.md` là hợp đồng thiết kế database v1.0. Khi khác nhau, ghi rõ lựa chọn triển khai và giới hạn, không âm thầm xóa yêu cầu hay coi dữ liệu demo là thiết kế production.
7. **Duy trì ngữ cảnh:** Khi cập nhật database, đồng bộ field, số bảng, quan hệ Mermaid, phiên bản và trạng thái triển khai trong tài liệu liên quan. Không tạo lại file ở đường dẫn cũ `docs/analysis-and-design/database_design.md`; file hiện nằm ở thư mục gốc.
