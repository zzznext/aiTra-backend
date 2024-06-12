USE aiTra;
CREATE TABLE IF NOT EXISTS Users (
                                     user_id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL,
                                     email VARCHAR(100) NOT NULL,
                                     password_hash VARCHAR(255) NOT NULL,
                                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Translations 表
CREATE TABLE IF NOT EXISTS Translations (
                                            translation_id INT AUTO_INCREMENT PRIMARY KEY,
                                            user_email VARCHAR(100) NOT NULL ,
                                            provider_name VARCHAR(100) NOT NULL,
                                            source_text TEXT NOT NULL,
                                            translated_text TEXT NOT NULL,
                                            source_lang_name VARCHAR(50) NOT NULL,
                                            target_lang_name VARCHAR(50) NOT NULL,
                                            status VARCHAR(50) NOT NULL,
                                            translated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP

);
