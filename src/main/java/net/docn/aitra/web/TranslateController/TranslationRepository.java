package net.docn.aitra.web.TranslateController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.docn.aitra.web.generator.domain.Translations;
import net.docn.aitra.web.generator.mapper.TranslationsMapper;
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
    private TranslationsMapper translationsMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /*
    *getTranslationsByUserEmail条件分页查询
    * 实现历史记录的分页查询
    * 2024年6月19日08:55:04
    * author zzznext
     */
    public IPage<Translations> getTranslationsByUserEmail(String userEmail,long current) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_email",userEmail);
        IPage<Translations> page = new Page<>(current,10);
        IPage<Translations> translationsIPage = translationsMapper.selectPage(page,wrapper);
        return  translationsIPage;
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
