package com.yafey.security.core.validate.code;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class ImageCode extends ValidateCode {
	
	@Setter @Getter
    private BufferedImage image;
    
    // 注意：构造方法中的 expireTime 是 int 型，多少秒，所以 成员变量的过期时间应该为 当前时间 + 多少秒过期 来构建一个 未来的时间。 
    public ImageCode(BufferedImage image, String code, int expireTime) {
    	super(code, expireTime);
        this.image = image;
    }
    
    public ImageCode(BufferedImage image, String code, LocalDateTime expireTime) {
    	super(code, expireTime);
        this.image = image;
    }
    
}
