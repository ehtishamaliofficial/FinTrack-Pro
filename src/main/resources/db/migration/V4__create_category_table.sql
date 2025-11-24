CREATE TABLE category (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          icon VARCHAR(100),
                          color VARCHAR(50),
                          type VARCHAR(20) NOT NULL,    -- INCOME / EXPENSE
                          is_system BOOLEAN NOT NULL DEFAULT FALSE,

                          user_id BIGINT,               -- NULL if system-created
                          CONSTRAINT fk_category_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users (id)
                                  ON DELETE CASCADE,

                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);

-- Ensure category name is unique FOR SAME USER OR SYSTEM
-- Allow same name for two users but not duplicate system-defined names
CREATE UNIQUE INDEX uniq_category_name_user
    ON category (name, user_id);
