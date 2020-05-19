package ru.spliterash.pcmasterserver.secutiry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.database.PreparedQueries;

@Component
public class PcMasterDetailsService implements UserDetailsService {
    @Autowired
    private PreparedQueries queries;
    private ResultSetExtractor<PcMasterUser> extractor = set -> {
        if (set.next()) {
            UserStatus status;
            String statusStr = set.getString("type");
            if (statusStr == null)
                status = UserStatus.USER;
            else
                status = UserStatus.valueOf(statusStr);
            return new PcMasterUser(set.getInt("id"),
                    set.getString("first_name"),
                    set.getString("last_name"),
                    set.getString("middle_name"),
                    set.getString("username"),
                    set.getString("password"),
                    status
            );
        } else
            return null;
    };

    @Override
    public PcMasterUser loadUserByUsername(String s) throws UsernameNotFoundException {
        JdbcTemplate t = queries.getTemplate().getJdbcTemplate();
        PcMasterUser user = t.query(
                "SELECT a.id, a.first_name, a.last_name, a.middle_name,a.password ,a.username, at.type " +
                        "from account a left join account_type at on at.id = a.account_type_id where username = ?", extractor, s);
        if (user != null)
            return user;
        else
            throw new UsernameNotFoundException("Not found");
    }

    public PcMasterUser loadUserById(int id) {
        JdbcTemplate t = queries.getTemplate().getJdbcTemplate();
        PcMasterUser user = t.query(
                "SELECT a.id, a.first_name, a.last_name, a.middle_name,a.password ,a.username, at.type " +
                        "from account a left join account_type at on at.id = a.account_type_id where a.id = ?", extractor, id);
        if (user != null)
            return user;
        else
            throw new UsernameNotFoundException("Not found");
    }
}
