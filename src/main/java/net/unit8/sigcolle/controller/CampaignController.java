package net.unit8.sigcolle.controller;

import javax.inject.Inject;
import javax.transaction.Transactional;

import enkan.collection.Multimap;
import enkan.component.doma2.DomaProvider;
import enkan.data.Flash;
import enkan.data.HttpResponse;
import enkan.data.Session;
import kotowari.component.TemplateEngine;
import net.unit8.sigcolle.auth.LoginUserPrincipal;
import net.unit8.sigcolle.dao.CampaignDao;
import net.unit8.sigcolle.dao.SignatureDao;
import net.unit8.sigcolle.dao.UserDao;
import net.unit8.sigcolle.form.CampaignForm;
import net.unit8.sigcolle.form.RegisterForm;
import net.unit8.sigcolle.form.SignatureForm;
import net.unit8.sigcolle.model.Campaign;
import net.unit8.sigcolle.model.User;
import net.unit8.sigcolle.model.UserCampaign;
import net.unit8.sigcolle.model.Signature;

import java.util.List;

import static enkan.util.BeanBuilder.builder;
import static enkan.util.HttpResponseUtils.RedirectStatusCode.SEE_OTHER;
import static enkan.util.HttpResponseUtils.redirect;
import static enkan.util.ThreadingUtils.some;

/**
 * @author kawasima
 */
public class CampaignController {
    @Inject
    private TemplateEngine templateEngine;

    @Inject
    private DomaProvider domaProvider;

    private HttpResponse showCampaign(Long campaignId, SignatureForm signature, String message) {
        CampaignDao campaignDao = domaProvider.getDao(CampaignDao.class);
        UserCampaign campaign = campaignDao.selectById(campaignId);

        SignatureDao signatureDao = domaProvider.getDao(SignatureDao.class);
        int signatureCount = signatureDao.countByCampaignId(campaignId);

        return templateEngine.render("campaign",
                "campaign", campaign,
                "signatureCount", signatureCount,
                "signature", signature,
                "message", message
        );
    }

    /**
     * キャンペーン詳細画面表示.
     * @param form URLパラメータ
     * @param flash flash scope session
     * @return HttpResponse
     */
    public HttpResponse index(CampaignForm form, Flash flash) {
        if (form.hasErrors()) {
            return builder(HttpResponse.of("Invalid"))
                    .set(HttpResponse::setStatus, 400)
                    .build();
        }

        return showCampaign(form.getCampaignIdLong(),
                new SignatureForm(),
                (String) some(flash, Flash::getValue).orElse(null));
    }

    /**
     * 署名の追加処理.
     * @param form 画面入力された署名情報.
     * @return HttpResponse
     */
    @Transactional
    public HttpResponse sign(SignatureForm form) {
        if (form.hasErrors()) {
            return showCampaign(form.getCampaignIdLong(), form, null);
        }
        Signature signature = builder(new Signature())
                .set(Signature::setCampaignId, form.getCampaignIdLong())
                .set(Signature::setName, form.getName())
                .set(Signature::setSignatureComment, form.getSignatureComment())
                .build();
        SignatureDao signatureDao = domaProvider.getDao(SignatureDao.class);
        signatureDao.insert(signature);

        return builder(redirect("/campaign/" + form.getCampaignId(), SEE_OTHER))
                .set(HttpResponse::setFlash, new Flash("ご賛同ありがとうございました！"))
                .build();
    }

    /**
     * 新規キャンペーン作成画面表示.
     * @return HttpResponse
     */
    public HttpResponse createForm() {
        return templateEngine.render("signature/new","new", new CampaignForm());
    }

    /**
     * 新規キャンペーン作成処理.
     * @return HttpResponse
     */


    public HttpResponse create(CampaignForm form)  {
        // TODO: create campaign
        if (form.hasErrors()) {
           return templateEngine.render("signature/new", "new", form);
        }
        CampaignDao campaignDao = domaProvider.getDao(CampaignDao.class);



        Campaign campaign = builder(new Campaign())
                .set(Campaign::setTitle, form.getTitle())
                .set(Campaign::setStatement, form.getStatement())
                .set(Campaign::setGoal, form.getGoal())
                .set(Campaign::setCreateUserId, form.getCreateUserId())
                .build();
        campaignDao.insert(campaign);




        //return builder(redirect("/", SEE_OTHER)).build();
        return builder(redirect("/campaign/" + campaign.getCampaignId(), SEE_OTHER))
                .set(HttpResponse::setFlash, new Flash("キャンペーン作成ありがとうございました！"))
                .build();
    }
}
