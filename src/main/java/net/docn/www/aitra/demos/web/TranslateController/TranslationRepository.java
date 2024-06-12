package net.docn.www.aitra.demos.web.TranslateController;

import net.docn.www.aitra.demos.web.TranslateController.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TranslationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Translation> getTranslationsByUserEmail(String userEmail) {
        String sql = "SELECT * FROM translations WHERE user_email = ?";
        return jdbcTemplate.query(sql, new Object[]{userEmail}, new TranslationRowMapper());
    }

    private static class TranslationRowMapper implements RowMapper<Translation> {
        @Override
        public Translation mapRow(ResultSet rs, int rowNum) throws SQLException {
            Translation translation = new Translation();
            translation.setTranslationId(rs.getLong("translation_id"));
            translation.setUserEmail(rs.getString("user_email"));
            translation.setProviderName(rs.getString("provider_name"));
            translation.setSourceText(rs.getString("source_text"));
            translation.setTranslatedText(rs.getString("translated_text"));
            translation.setSourceLangName(rs.getString("source_lang_name"));
            translation.setTargetLangName(rs.getString("target_lang_name"));
            translation.setStatus(rs.getString("status"));
            translation.setTranslatedAt(rs.getTimestamp("translated_at").toLocalDateTime());
            translation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return translation;
        }
    }
}
