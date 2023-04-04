package org.tcollignon.user.utils;

public class MailUtils {

    public static String getReinitPasswordMailHtml(String link, String logoPath) {
        String html = "<html>" +
            "<body>\n" +
            "                        <h2 style=\"margin-bottom:0!important;font-weight:500!important\">\n" +
            "                          Votre demande de changement de mot de passe\n" +
            "                        </h2>\n" +
            "                      Vous avez demand&eacute; &agrave; r&eacute;initialiser votre mot de passe. Cliquez sur le lien ci-dessous pour en choisir un nouveau.\n" +
            "                        <a href=\"" + link + "\" target=\"_blank\">Changer mon mot de passe</a>\n" +
            "                        <br>The Devoxx Team\n" +
            "                    </body>" +
            "</html>";
        return html;
    }

    public static String getRegisterMailHtml(String nickname) {
        String html = "<html>" +
            "<body> Bienvenue &agrave; Devoxx " + nickname +
            "</body>" +
            "</html>";
        return html;
    }

}
