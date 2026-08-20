# Baseline Azure PostgreSQL với Flyway

Migration `V1__iam_baseline.sql` mô tả schema IAM hiện tại để dựng database trống. Tuyệt đối không chạy `V1` trực tiếp trên Azure database đã có 7 bảng.

## Database Azure hiện tại

1. Giữ `FLYWAY_ENABLED=false` trong runtime hiện tại.
2. Tạo backup có thể phục hồi và ghi lại schema/database đích.
3. Đối chiếu schema Azure với `V1`: extensions, 7 tables, primary/check constraints, 8 foreign keys, 27 indexes và 7 triggers.
4. Dùng Flyway CLI trong phiên vận hành được kiểm soát, cung cấp connection bằng biến môi trường để không ghi password vào command history.
5. Tạo baseline ở version `1`, không dùng `migrate` để chạy `V1`:

   ```bash
   flyway -baselineVersion=1 -baselineDescription="IAM existing schema" baseline
   flyway validate
   ```

6. Xác nhận `flyway_schema_history` chứa baseline version `1` và ứng dụng vẫn khởi động với `ddl-auto=validate`.
7. Chỉ sau bước này mới bật Flyway trong deployment. Migration thay đổi schema tiếp theo phải bắt đầu từ `V2`.

Không dùng `baselineOnMigrate` thường trực: tùy chọn đó có thể che việc trỏ nhầm vào một database chưa được quản lý.

## Database trống

Database role phải có quyền tạo extension `citext`, `pgcrypto` và các object trong schema `public`. Sau đó bật:

```properties
FLYWAY_ENABLED=true
```

Flyway sẽ chạy `V1` và tạo schema IAM, không tạo seed data. Dữ liệu app/role/permission nếu cần phải được quản lý bằng migration hoặc bootstrap riêng sau `V1`.

## Boundary dữ liệu tương lai

- IAM tiếp tục sở hữu các bảng hiện tại trong `public` cho đến khi có migration chuyển schema riêng.
- TikBack, TroHub và SalonFlow dùng schema riêng khi bắt đầu triển khai.
- Product module chỉ lưu IAM identifier dạng UUID; không tạo foreign key trực tiếp giữa hai product schema.
- Các invariant xuyên bảng như role/permission cùng app chưa được database hiện tại cưỡng chế. Chỉ bổ sung constraint sau khi audit dữ liệu và phát hành migration riêng.
