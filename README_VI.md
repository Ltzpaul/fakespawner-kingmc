# KingSMP Fake Skeleton Spawner Mod (Minecraft 1.21.11 - Fabric)

Mod tái hiện chính xác 100% tính năng **biến Khối Đồng (`minecraft:copper_block`) thành Lồng triệu hồi Skeleton (`Lồng triệu hồi Skeleton`)** giống hệt trên server **KingSMP (`KINGMC.VN`)** trong video 34 giây.

---

## 1. Các tính năng đã tái hiện chính xác từ Video
1. **Biến Khối Đồng thành Lồng triệu hồi Skeleton**:
   - Tên vật phẩm màu xanh ngọc (`§b`): `Lồng triệu hồi Skeleton`
   - Lore bên dưới:
     - `Interact with Spawn Egg:` (Xám)
     - `Sets Mob Type` (Vàng cam)
   - Model trong túi đồ, Shulker Box, trên tay và khi đặt ra đất đều hiển thị là Lồng triệu hồi (`minecraft:spawner`) có **Skeleton xoay tròn bên trong** kèm hiệu ứng hạt lửa + khói.
2. **Cơ chế đặt và gộp lồng (`Stack Spawner`)**:
   - Đặt 1 Khối Đồng từ stack 64 -> Cả stack 64 được tính vào lồng đầu tiên.
   - Cầm các stack 64 tiếp theo chuột phải vào lồng -> Cộng dồn lên `128`, `192`, `256` và hiện đúng Action Bar:
     `Bạn đã đặt thêm [64] Lồng triệu hồi Skeleton , tổng [256]`
3. **Giao diện 6 hàng `256 Skeleton`**:
   - Khi cầm Cuốc (hoặc tay không) chuột phải vào lồng -> Mở GUI 6 hàng tiêu đề `256 Skeleton`.
   - 8 ô đầu chứa 8 stack 64 Xương (`512` xương).
   - Hàng cuối gồm: Mũi tên (ô 45), Kính xám (ô 46, 47, 51, 52), Túi Bundle (ô 48), Chai kinh nghiệm `NHẬN EXP` (ô 49), Lồng `THÔNG TIN` (ô 50), Đá chạm khắc (ô 53).
   - Bấm vào `NHẬN EXP` (ô 49):
     - Chuyển `ĐANG CÓ: 512/768K` thành `ĐANG CÓ: 0/768K`.
     - Hiện Action Bar `+EXP 512` và HUD góc trái `+$ 53.76K` / `+EXP 512`.
4. **Đập lồng hoàn trả đủ 4 stack 64**:
   - Khi đào vỡ khối đồng bằng cuốc -> Hiện Action Bar:
     `Bạn đã đập [256] Lồng triệu hồi Skeleton , Còn lại 0`
   - Trả lại đủ 4 stack 64 `Lồng triệu hồi Skeleton` vào thanh hotbar.

---

## 2. Hướng dẫn Build ra file `.jar` trên Windows 11
1. Giải nén file `fake-spawner-mod-1.21.11.zip` ra một thư mục bất kỳ.
2. Mở **PowerShell** tại thư mục vừa giải nén.
3. Chạy lệnh sau để tự động tải Gradle Wrapper và build ra file `.jar`:
   ```powershell
   powershell -ExecutionPolicy Bypass -File .\build-mod.ps1
   ```
   *(Hoặc chạy trực tiếp: `.\gradlew.bat build`)*
4. Sau khi build xong, lấy file `.jar` tại:
   `build\libs\fake-spawner-mod-1.0.0.jar`
   và bỏ vào thư mục `.minecraft\mods` (cùng với **Fabric API 1.21.11**).
