package net.unit8.sigcolle.form;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author kawasima
 */
@Data
public class CampaignForm extends FormBase {
    @DecimalMin("1")
    @DecimalMax("9999")
    private String campaignId;

    private String title;

    private String statement;

    private Long goal;

    private Long createUserId;

    public Long getCampaignIdLong() {
        return Long.parseLong(campaignId);
    }
}
