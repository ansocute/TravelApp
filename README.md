# 🌍 Travel App - Đồ án Phát triển Ứng dụng Di động

Ứng dụng hỗ trợ tìm kiếm địa điểm, lên kế hoạch du lịch và trải nghiệm bản đồ thông minh dành cho người dùng Android.

---

## 👥 Danh sách thành viên & Phân công nhiệm vụ

| Thành viên | Vai trò | Nhiệm vụ chính (Package tương ứng) | Sản phẩm bàn giao |
|:---|:---|:---|:---|
| **An (Leader)** | Maps & Location | `ui.map` & `services.LocationService` | Bản đồ hiện vị trí người dùng và các Marker địa điểm. |
| **Tùng** | Authentication | `ui.auth` & `services.AuthService` | Màn hình Đăng nhập, Đăng ký (Firebase hoặc API). |
| **Đức** | Discovery | `ui.discovery` & `data.repository` | Danh sách địa điểm (RecyclerView), thanh tìm kiếm và bộ lọc. |
| **Huy** | Planner | `ui.planner` & `data.local (Room)` | Lưu lịch trình chuyến đi vào máy, hiển thị danh sách ngày đi. |
| **Cương** | Details | `ui.details` | Trang chi tiết địa điểm (Ảnh, mô tả, đánh giá, bình luận). |

---

## 🛠 Hướng dẫn dành cho thành viên nhóm

### 1. Quy trình làm việc với Git
- **Nhánh chính (`main`):** Chỉ dùng để gộp code hoàn chỉnh sau khi đã qua kiểm tra.
- **Nhánh cá nhân:** Mỗi bạn tự tạo nhánh riêng để làm việc: `git checkout -b [ten-cua-ban]`.
- **Commit:** Ghi chú rõ ràng nội dung đã làm (Ví dụ: `git commit -m "Tung: Hoan thanh giao dien Login"`).

### 2. Cấu hình môi trường
Do vấn đề bảo mật, file chứa API Key không được đẩy lên GitHub. Các bạn cần:
1. Tạo file `local.properties` tại thư mục gốc dự án.
2. Thêm dòng sau vào file:
   ```properties
   MAPS_API_KEY=AIzaSy... (Liên hệ Leader An để lấy mã đầy đủ)
Nhấn Sync Project with Gradle Files (hình con voi 🐘) để nhận cấu hình mới.

🏗️ KIẾN TRÚC & CÔNG NGHỆ SỬ DỤNG
Kiến trúc: MVVM (Model - View - ViewModel) giúp tách biệt logic xử lý và giao diện.

Ngôn ngữ: Kotlin.

Thư viện: View Binding, Google Maps SDK, Room Database, Retrofit, Jetpack Navigation.

📝 NHẬT KÝ CẬP NHẬT (Changelog)
v1.0.0 (11/04/2026): - Khởi tạo cấu trúc thư mục chuẩn cho cả nhóm theo sơ đồ phân công.

Hoàn thiện luồng Auth cơ bản và tích hợp Maps SDK nền tảng.

Thiết lập cơ chế bảo mật API Key toàn cục. (By Quoc An)


---

