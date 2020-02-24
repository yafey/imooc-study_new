package com.yafey.security.core.validate.code;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageCode {
    private BufferedImage image;
    private String code;
    private LocalDateTime expireTime; // 过期时间
    
    // 注意：构造方法中的 expireTime 是 int 型，多少秒，所以 成员变量的过期时间应该为 当前时间 + 多少秒过期 来构建一个 未来的时间。 
    public ImageCode(BufferedImage image, String code, int expireTime) {
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireTime);
    }
    
	public boolean isExpried() {
		return LocalDateTime.now().isAfter(expireTime);
	}
}
