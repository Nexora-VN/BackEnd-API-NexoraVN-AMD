# Nexora VN Backend

Spring Boot 4, Java 21, Azure PostgreSQL và kiến trúc **modular monolith**.

## Chạy local

Ứng dụng dùng Azure PostgreSQL trực tiếp. Docker Compose, Redis và RabbitMQ chưa được kích hoạt.

```bash
cp .env.example .env
# Điền kết nối Azure PostgreSQL vào .env
./mvnw spring-boot:run
```

- JDBC URL nên có `sslmode=require`.
- Hibernate dùng `ddl-auto=validate`, không tự sửa schema.
- Flyway mặc định tắt để không tác động database Azure đã tồn tại.

## Kiến trúc module

```text
NexoraApplication
│
├── iam/                        # Đang triển khai, sở hữu các bảng IAM trong public
│   └── user/
│       ├── domain/             # User thuần Java
│       ├── application/
│       │   ├── port/in/        # 3 input use case
│       │   ├── port/out/       # UserRepository
│       │   ├── service/        # UserService
│       │   └── model/          # UserView
│       ├── web/                # REST controller và error handler
│       ├── persistence/        # JPA entity/repository/adapter
│       └── config/             # Composition root
│
├── tikback/                    # Chỉ tạo khi bắt đầu nghiệp vụ
├── trohub/                     # Chỉ tạo khi bắt đầu nghiệp vụ
└── salonflow/                  # Chỉ tạo khi bắt đầu nghiệp vụ
```

TikBack, TroHub và SalonFlow sau này là top-level module riêng. Mỗi module sở hữu package, schema và repository của nó; không import implementation hoặc ghi database của module khác. Giao tiếp xuyên module đi qua public application API hoặc domain event.

## Luồng User

```text
HTTP JSON
    ↓
CreateUserUseCase.Command
    ↓
UserService → User domain → UserRepository
                              ↓
                    UserPersistenceAdapter
                              ↓
                       UserJpaEntity
                              ↓
                     public.users (Azure)

Domain → UserView → HTTP JSON
```

Ba input port được giữ riêng để dễ tìm luồng nghiệp vụ:

- `CreateUserUseCase`
- `GetUserUseCase`
- `GetAllUsersUseCase`

Không có lớp request/response/mapper riêng cho HTTP. `Command` là input của create, còn `UserView` là output chung và không chứa `passwordHash`, trạng thái khóa hoặc số lần đăng nhập lỗi. Domain vẫn tách khỏi JPA để việc đổi database hoặc tách service không kéo persistence vào business logic.

## API hiện tại

```text
POST /api/v1/users
GET  /api/v1/users/{id}
GET  /api/v1/users
```

Danh sách chỉ trả user chưa soft-delete và sắp xếp mới nhất trước.

## Database migration

`V1__iam_baseline.sql` là schema-only baseline của 7 bảng IAM. File này không chứa dữ liệu từ dump, Azure role, owner, ACL, tablespace hoặc lệnh tạo database.

- Database mới: bật `FLYWAY_ENABLED=true` để chạy từ `V1`.
- Azure hiện tại: không bật Flyway trước khi hoàn tất quy trình baseline.

Xem [quy trình baseline Azure](docs/database-baseline.md) trước khi quản lý database hiện tại bằng Flyway.

## Kiểm tra

```bash
./mvnw -ntp test
```

Test hiện tại không cần Azure DB hoặc Docker. Spring Modulith và ArchUnit khóa boundary giữa module/layer; MockMvc khóa contract của ba User API.
