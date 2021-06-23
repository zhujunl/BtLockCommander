package com.miaxis.btlockcommanderdemo.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class Config {

    @Id
    private Long id;
    private String baseUrl;
    private String username;
    private String password;
    private String hostCertificate;
    @Generated(hash = 592844077)
    public Config(Long id, String baseUrl, String username, String password,
            String hostCertificate) {
        this.id = id;
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        this.hostCertificate = hostCertificate;
    }
    @Generated(hash = 589037648)
    public Config() {
    }

    private Config(Builder builder) {
        setId(builder.id);
        setBaseUrl(builder.baseUrl);
        setUsername(builder.username);
        setPassword(builder.password);
        setHostCertificate(builder.hostCertificate);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBaseUrl() {
        return this.baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getHostCertificate() {
        return this.hostCertificate;
    }
    public void setHostCertificate(String hostCertificate) {
        this.hostCertificate = hostCertificate;
    }

    public static final class Builder {
        private Long id;
        private String baseUrl;
        private String username;
        private String password;
        private String hostCertificate;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder baseUrl(String val) {
            baseUrl = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder hostCertificate(String val) {
            hostCertificate = val;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }
}
