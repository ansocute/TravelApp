🌍 Travel App - Smart Trip Companion
Ứng dụng hỗ trợ tìm kiếm địa điểm, lên kế hoạch du lịch và trải nghiệm bản đồ thông minh dành cho người dùng Android. Được xây dựng trên nền tảng Kotlin với kiến trúc hiện đại, tập trung vào trải nghiệm người dùng mượt mà.

✨ Tính năng nổi bật
1. Khám phá địa điểm (Discovery)
Smart Search: Tìm kiếm địa điểm du lịch, nhà hàng, khách sạn thông minh.

Bộ lọc đa dạng: Phân loại địa điểm theo khoảng cách, xếp hạng và loại hình dịch vụ.

Danh sách xu hướng: Gợi ý các điểm đến hot nhất dựa trên đánh giá của cộng đồng.

2. Bản đồ tương tác (Interactive Map)
Custom Silver Theme: Giao diện bản đồ tối giản, dễ nhìn, tối ưu cho việc quan sát Marker.

Real-time Location: Tự động xác định và hiển thị vị trí chính xác của người dùng trên bản đồ.

Bottom Sheet Details: Chạm vào Marker để xem nhanh thông tin địa điểm mà không cần chuyển màn hình.

3. Lập kế hoạch chuyến đi (Trip Planner)
Lịch trình cá nhân: Tạo và quản lý danh sách các điểm đến theo từng ngày.

Lưu trữ ngoại tuyến: Hỗ trợ xem lại lịch trình ngay cả khi không có kết nối Internet (Sử dụng Room Database).

4. Quản lý tài khoản (Authentication)
Bảo mật: Đăng ký và đăng nhập an toàn, hỗ trợ ghi nhớ phiên làm việc.

Hồ sơ cá nhân: Quản lý thông tin, ảnh đại diện và các địa điểm đã yêu thích.

🏗️ Kiến trúc hệ thống
Dự án áp dụng mô hình Single Activity Architecture kết hợp với MVVM Pattern:

MainActivity: Đóng vai trò Host, điều phối việc chuyển đổi giữa các Fragment thông qua thanh điều hướng phía dưới.

Fragments: Tách biệt logic từng màn hình (Explore, Map, Planner, Profile) giúp tối ưu bộ nhớ và hiệu suất.

ViewBinding: Tương tác với UI an toàn, tránh lỗi Null Pointer Exception.

👥 Phân công nhiệm vụ (Package Structure)
Thành viên,Nhiệm vụ chính,Chi tiết công việc,Trạng thái
An (Leader),Core & Maps,- [x] Khởi tạo Single Activity & Bottom Navigation- [x] Tích hợp Maps SDK & Silver Theme- [x] Xử lý Location Service & Marker,🟢 Hoàn thành
Tùng,Authentication & Core,- [ ] Giao diện Login/Register/ForgotPassword- [ ] Kết nối Firebase Auth/API- [ ] Quản lý User Session - [ ] Giao diện Profile người dùng, chỉnh sửa profile,🟢 Hoàn thành
Đức,Discovery,- [ ] RecyclerView danh sách địa điểm- [ ] SearchBar & Filter logic- [ ] Fetch data từ Repository,🟡 Đang làm
Huy,Planner,- [ ] Thiết kế Database (Room)- [ ] Tính năng thêm/xóa lịch trình- [ ] Hiển thị danh sách chuyến đi,🟡 Đang làm
Cường,Details,- [ ] Layout chi tiết địa điểm- [ ] Xử lý Image Slider & Reviews- [ ] Tính năng chia sẻ/yêu thích,🟡 Đang làm

⚙️ Cài đặt & Sử dụng
Clone dự án.

Thêm MAPS_API_KEY vào file local.properties.

Build > Clean Project và Sync Gradle.
