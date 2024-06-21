package net.docn.aitra.web.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.docn.aitra.web.generator.domain.Users;
import net.docn.aitra.web.generator.service.UsersService;
import net.docn.aitra.web.generator.mapper.UsersMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【users】的数据库操作Service实现
* @createDate 2024-06-18 21:05:44
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

}




