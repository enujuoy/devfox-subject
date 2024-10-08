package domain.dto;

import domain.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCntDto {

    private Long totalUserCnt;
    private Long totalAdminCnt;
    private Long totalBronzeCnt;
    private Long totalSilverCnt;
    private Long totalGoldCnt;
    private Long totalBlacklistCnt;
}
