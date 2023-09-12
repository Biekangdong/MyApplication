package com.xixia.aiimageupload.meitu;

import java.util.List;

/**
 * @ClassName ImageBean
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/8/1 19:53
 * @Version 1.0
 * @UpdateDate 2023/8/1 19:53
 * @UpdateRemark 更新说明
 */
public class ImageBean {

    public ParameterDTO parameter;
    public List<MediaInfoListDTO> media_info_list;

    public static class ParameterDTO {
        public String rsp_media_type;
        public FreeExpandRatioDTO free_expand_ratio;
        public int seed;
        public static class FreeExpandRatioDTO {
            public double left;
            public int right;
            public double top;
            public double bottom;
        }
    }

    public static class MediaInfoListDTO {
        public String media_data;
        public MediaProfilesDTO media_profiles;

        public static class MediaProfilesDTO {
            public String media_data_type;
        }
    }
}
