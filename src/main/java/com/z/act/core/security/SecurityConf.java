package com.z.act.core.security;

import com.z.act.core.security.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConf extends WebSecurityConfigurerAdapter {
    @Autowired
    private SysUserService sysUserService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(sysUserService).passwordEncoder(new PasswordEncoder() {//密码加密
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return s.equals(charSequence.toString());
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/css/**", "/js/**").permitAll(); //css和js允许匿名
        http.authorizeRequests().and().formLogin().and().logout().permitAll();//登陆登出无需认证
        http.authorizeRequests().anyRequest().authenticated();//其他所有请求需要认证
        http.csrf().disable();// 关闭CSRF跨域
    }

}
