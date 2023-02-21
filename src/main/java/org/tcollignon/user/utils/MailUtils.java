package org.tcollignon.user.utils;

public class MailUtils {

    public static String getReinitPasswordMailHtml(String link, String logoPath) {
        String html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head>" +
            "<body><div class=\"aHl\"></div><div id=\":1wb\" tabindex=\"-1\"></div><div id=\":1w0\" class=\"ii gt\" jslog=\"20277; u014N:xr6bB; 4:W251bGwsbnVsbCxbXV0.\"><div id=\":1vz\" class=\"a3s aiL msg3674222380842298668\"><div class=\"adM\">\n" +
            "  \n" +
            "  \n" +
            "\n" +
            "  </div><div style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;width:100%!important;height:100%;line-height:1.6em;background-color:#fafafa;margin:0;color:#3d5170\" bgcolor=\"#fafafa\"><div class=\"adM\">\n" +
            "    </div><table style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;width:100%;background-color:#fafafa;margin:0\" bgcolor=\"#fafafa\">\n" +
            "      <tbody><tr style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;margin:0\">\n" +
            "        <td style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;vertical-align:top;margin:0\" valign=\"top\"></td>\n" +
            "        <td class=\"m_3674222380842298668container\" style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;vertical-align:top;display:block!important;max-width:600px!important;clear:both!important;margin:0 auto\" width=\"600\" valign=\"top\">\n" +
            "          <div class=\"m_3674222380842298668content\" style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;max-width:600px;display:block;margin:0 auto;padding:20px\">\n" +
            "            <table style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;margin:0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
            "              <tbody>\n" +
            "                <tr>\n" +
            "                  <th style=\"font-weight:400;text-align:left;vertical-align:top\" width=\"100%\">\n" +
            "                    <table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"20\" border=\"0\">\n" +
            "                      <tbody><tr>\n" +
            "                        <td>\n" +
            "                          <div style=\"line-height:10px\" align=\"center\">\n" +
            "                            <img alt=\"Devoxx\" src=\"" + logoPath + "\" style=\"height:72px;max-width:100%;margin:0 auto;padding:0;border:0;text-align:center\" title=\"Devoxx 2023\" class=\"CToWUd\" height=\"72\">\n" +
            "                            \n" +
            "                          </div>\n" +
            "                        </td>\n" +
            "                      </tr>\n" +
            "                    </tbody></table>\n" +
            "                  </th>\n" +
            "                </tr>\n" +
            "              </tbody>\n" +
            "            </table>\n" +
            "\n" +
            "            <table style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;background-color:#fff;margin:0;border:1px solid #e9e9e9;border-radius:7px\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#fff\">\n" +
            "              <tbody><tr style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;margin:0\">\n" +
            "                <td class=\"m_3674222380842298668content-wrap\" style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;vertical-align:top;margin:0;padding:32px\" valign=\"top\">\n" +
            "                  \n" +
            "                  <table style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;margin:0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
            "                    <tbody><tr style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;margin:0\">\n" +
            "                      <td style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;vertical-align:top;margin:0;padding:0 0 20px\" valign=\"top\">\n" +
            "                        <h2 style=\"margin-bottom:0!important;font-weight:500!important\">\n" +
            "                          Votre demande de changement de mot de passe\n" +
            "                        </h2>\n" +
            "                      </td>\n" +
            "                    </tr>\n" +
            "                    <tr style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;margin:0\">\n" +
            "                      <td style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;vertical-align:top;margin:0;padding:0 0 20px\" valign=\"top\">\n" +
            "                      Vous avez demand&eacute; &agrave; r&eacute;initialiser votre mot de passe. Cliquez sur le lien ci-dessous pour en choisir un nouveau.\n" +
            "                      </td>\n" +
            "                    </tr>\n" +
            "                    <tr style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;margin:0\">\n" +
            "                      <td style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;vertical-align:top;margin:0;padding:0 0 20px;text-align:center\" valign=\"top\">\n" +
            "                        <a href=\"" + link + "\" style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;color:#fff;text-decoration:none;line-height:2em;text-align:center;display:inline-block;border-radius:5px;background-color:#1f43f6;margin:0;border-color:#1f43f6;border-style:solid;border-width:10px 20px\" target=\"_blank\">Changer mon mot de passe</a>\n" +
            "                      </td>\n" +
            "                    </tr>\n" +
            "                    <tr style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;margin:0\">\n" +
            "                      <td style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;vertical-align:top;margin:0;padding:0\" valign=\"top\">\n" +
            "                        <br>The Devoxx Team\n" +
            "                      </td>\n" +
            "                    </tr>\n" +
            "                    <tr style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;margin:0\">\n" +
            "                      <td style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;vertical-align:top;margin:0;padding:0\" valign=\"top\">\n" +
            "                        <br><br><i>Si le bouton ne fonctionne pas, copier/collez ce lien dans votre navigateur web : " + link + "</i>\n" +
            "                      </td>\n" +
            "                    </tr>\n" +
            "                  </tbody></table>\n" +
            "                </td>\n" +
            "              </tr>\n" +
            "            </tbody></table>\n" +
            "          </div>\n" +
            "        </td>\n" +
            "        <td style=\"font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;box-sizing:border-box;font-size:14px;vertical-align:top;margin:0\" valign=\"top\"></td>\n" +
            "      </tr>\n" +
            "    </tbody></table>\n" +
            "\n" +
            "</div></div></div><div id=\":1we\" class=\"ii gt\" style=\"display:none\"><div id=\":1wf\" class=\"a3s aiL \"></div></div><div class=\"hi\"></div>" +
            "</body>" +
            "</html>";
        return html;
    }

    public static String getRegisterMailHtml(String nickname) {
        String html = "<html>" +
            "<body> Bienvenu &agrave; Devoxx " + nickname +
            "</body>" +
            "</html>";
        return html;
    }

}
