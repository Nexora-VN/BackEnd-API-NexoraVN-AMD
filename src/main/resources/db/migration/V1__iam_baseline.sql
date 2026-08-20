-- Schema-only IAM baseline extracted from the 2026-08-20 Azure PostgreSQL dump.
-- Existing databases must be baselined at version 1; do not execute this file on them.

CREATE EXTENSION IF NOT EXISTS citext WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;

CREATE FUNCTION public.fn_set_updated_at() RETURNS trigger
    LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

CREATE TABLE public.apps (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    code varchar(50) NOT NULL,
    name varchar(100) NOT NULL,
    description text,
    slug varchar(100),
    base_path varchar(150),
    status varchar(30) DEFAULT 'ACTIVE' NOT NULL,
    is_public boolean DEFAULT false NOT NULL,
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by uuid,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by uuid,
    deleted_at timestamptz,
    deleted_by uuid,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT apps_pkey PRIMARY KEY (id),
    CONSTRAINT apps_status_check CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE'))
);

CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    email public.citext NOT NULL,
    phone varchar(20),
    username public.citext,
    password_hash varchar(255),
    full_name varchar(150) NOT NULL,
    display_name varchar(150),
    avatar_url text,
    status varchar(30) DEFAULT 'ACTIVE' NOT NULL,
    email_verified_at timestamptz,
    phone_verified_at timestamptz,
    last_login_at timestamptz,
    failed_login_count integer DEFAULT 0 NOT NULL,
    locked_until timestamptz,
    locale varchar(20) DEFAULT 'vi-VN',
    timezone varchar(50) DEFAULT 'Asia/Ho_Chi_Minh',
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by uuid,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by uuid,
    deleted_at timestamptz,
    deleted_by uuid,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_failed_login_count_check CHECK (failed_login_count >= 0),
    CONSTRAINT users_status_check CHECK (status IN ('PENDING', 'ACTIVE', 'BLOCKED', 'SUSPENDED', 'DISABLED'))
);

CREATE TABLE public.organizations (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    app_id uuid NOT NULL,
    code varchar(50) NOT NULL,
    slug varchar(100),
    name varchar(200) NOT NULL,
    type varchar(50) NOT NULL,
    description text,
    status varchar(30) DEFAULT 'ACTIVE' NOT NULL,
    email public.citext,
    phone varchar(20),
    address_line varchar(255),
    ward varchar(100),
    district varchar(100),
    province varchar(100),
    country varchar(100) DEFAULT 'Vietnam',
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by uuid,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by uuid,
    deleted_at timestamptz,
    deleted_by uuid,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT organizations_pkey PRIMARY KEY (id),
    CONSTRAINT organizations_status_check CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'INACTIVE'))
);

CREATE TABLE public.roles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    app_id uuid,
    code varchar(100) NOT NULL,
    name varchar(150) NOT NULL,
    description text,
    role_type varchar(30) DEFAULT 'SYSTEM' NOT NULL,
    status varchar(30) DEFAULT 'ACTIVE' NOT NULL,
    is_system boolean DEFAULT false NOT NULL,
    is_default boolean DEFAULT false NOT NULL,
    priority integer DEFAULT 100 NOT NULL,
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by uuid,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by uuid,
    deleted_at timestamptz,
    deleted_by uuid,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id),
    CONSTRAINT roles_role_type_check CHECK (role_type IN ('SYSTEM', 'CUSTOM')),
    CONSTRAINT roles_status_check CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE public.permissions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    app_id uuid NOT NULL,
    code varchar(150) NOT NULL,
    resource varchar(100) NOT NULL,
    action varchar(50) NOT NULL,
    name varchar(150),
    description text,
    status varchar(30) DEFAULT 'ACTIVE' NOT NULL,
    is_system boolean DEFAULT true NOT NULL,
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by uuid,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by uuid,
    deleted_at timestamptz,
    deleted_by uuid,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT permissions_pkey PRIMARY KEY (id),
    CONSTRAINT permissions_status_check CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE public.user_roles (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    organization_id uuid,
    status varchar(30) DEFAULT 'ACTIVE' NOT NULL,
    valid_from timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    valid_until timestamptz,
    note text,
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by uuid,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by uuid,
    deleted_at timestamptz,
    deleted_by uuid,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT user_roles_pkey PRIMARY KEY (id),
    CONSTRAINT chk_user_roles_valid_time CHECK (valid_until IS NULL OR valid_until > valid_from),
    CONSTRAINT user_roles_status_check CHECK (status IN ('ACTIVE', 'INACTIVE', 'REVOKED'))
);

CREATE TABLE public.role_permissions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    role_id uuid NOT NULL,
    permission_id uuid NOT NULL,
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by uuid,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by uuid,
    deleted_at timestamptz,
    deleted_by uuid,
    CONSTRAINT role_permissions_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_organizations_app ON public.organizations (app_id);
CREATE INDEX idx_organizations_status ON public.organizations (status);
CREATE INDEX idx_permissions_app ON public.permissions (app_id);
CREATE INDEX idx_permissions_resource ON public.permissions (resource);
CREATE INDEX idx_role_permissions_permission ON public.role_permissions (permission_id);
CREATE INDEX idx_role_permissions_role ON public.role_permissions (role_id);
CREATE INDEX idx_roles_app ON public.roles (app_id);
CREATE INDEX idx_user_roles_lookup ON public.user_roles (user_id, organization_id, status);
CREATE INDEX idx_user_roles_organization ON public.user_roles (organization_id);
CREATE INDEX idx_user_roles_role ON public.user_roles (role_id);
CREATE INDEX idx_user_roles_user ON public.user_roles (user_id);
CREATE INDEX idx_users_deleted_at ON public.users (deleted_at);
CREATE INDEX idx_users_status ON public.users (status);

CREATE UNIQUE INDEX uq_apps_code ON public.apps (upper(code::text))
    WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uq_apps_slug ON public.apps (slug)
    WHERE slug IS NOT NULL AND deleted_at IS NULL;
CREATE UNIQUE INDEX uq_organizations_app_code ON public.organizations (app_id, upper(code::text))
    WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uq_organizations_app_slug ON public.organizations (app_id, slug)
    WHERE slug IS NOT NULL AND deleted_at IS NULL;
CREATE UNIQUE INDEX uq_permissions_app_code ON public.permissions (app_id, lower(code::text))
    WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uq_permissions_app_resource_action
    ON public.permissions (app_id, lower(resource::text), lower(action::text))
    WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uq_role_permissions ON public.role_permissions (role_id, permission_id)
    WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uq_roles_app_code ON public.roles (app_id, upper(code::text))
    WHERE app_id IS NOT NULL AND deleted_at IS NULL;
CREATE UNIQUE INDEX uq_roles_global_code ON public.roles (upper(code::text))
    WHERE app_id IS NULL AND deleted_at IS NULL;
CREATE UNIQUE INDEX uq_user_roles_global ON public.user_roles (user_id, role_id)
    WHERE organization_id IS NULL AND deleted_at IS NULL;
CREATE UNIQUE INDEX uq_user_roles_organization ON public.user_roles (user_id, role_id, organization_id)
    WHERE organization_id IS NOT NULL AND deleted_at IS NULL;
CREATE UNIQUE INDEX uq_users_email ON public.users (email)
    WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uq_users_phone ON public.users (phone)
    WHERE phone IS NOT NULL AND deleted_at IS NULL;
CREATE UNIQUE INDEX uq_users_username ON public.users (username)
    WHERE username IS NOT NULL AND deleted_at IS NULL;

ALTER TABLE public.organizations
    ADD CONSTRAINT fk_organizations_app FOREIGN KEY (app_id)
        REFERENCES public.apps (id) ON DELETE RESTRICT;
ALTER TABLE public.permissions
    ADD CONSTRAINT fk_permissions_app FOREIGN KEY (app_id)
        REFERENCES public.apps (id) ON DELETE RESTRICT;
ALTER TABLE public.role_permissions
    ADD CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id)
        REFERENCES public.permissions (id) ON DELETE RESTRICT;
ALTER TABLE public.role_permissions
    ADD CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id)
        REFERENCES public.roles (id) ON DELETE RESTRICT;
ALTER TABLE public.roles
    ADD CONSTRAINT fk_roles_app FOREIGN KEY (app_id)
        REFERENCES public.apps (id) ON DELETE RESTRICT;
ALTER TABLE public.user_roles
    ADD CONSTRAINT fk_user_roles_organization FOREIGN KEY (organization_id)
        REFERENCES public.organizations (id) ON DELETE RESTRICT;
ALTER TABLE public.user_roles
    ADD CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
        REFERENCES public.roles (id) ON DELETE RESTRICT;
ALTER TABLE public.user_roles
    ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES public.users (id) ON DELETE RESTRICT;

CREATE TRIGGER trg_apps_updated_at
    BEFORE UPDATE ON public.apps
    FOR EACH ROW EXECUTE FUNCTION public.fn_set_updated_at();
CREATE TRIGGER trg_organizations_updated_at
    BEFORE UPDATE ON public.organizations
    FOR EACH ROW EXECUTE FUNCTION public.fn_set_updated_at();
CREATE TRIGGER trg_permissions_updated_at
    BEFORE UPDATE ON public.permissions
    FOR EACH ROW EXECUTE FUNCTION public.fn_set_updated_at();
CREATE TRIGGER trg_role_permissions_updated_at
    BEFORE UPDATE ON public.role_permissions
    FOR EACH ROW EXECUTE FUNCTION public.fn_set_updated_at();
CREATE TRIGGER trg_roles_updated_at
    BEFORE UPDATE ON public.roles
    FOR EACH ROW EXECUTE FUNCTION public.fn_set_updated_at();
CREATE TRIGGER trg_user_roles_updated_at
    BEFORE UPDATE ON public.user_roles
    FOR EACH ROW EXECUTE FUNCTION public.fn_set_updated_at();
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON public.users
    FOR EACH ROW EXECUTE FUNCTION public.fn_set_updated_at();
