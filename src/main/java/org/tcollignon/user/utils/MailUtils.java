package org.tcollignon.user.utils;

public class MailUtils {

    public static String getReinitPasswordMailHtml(String link, String logoPath) {
        String html = "<html>" +
            "<body>\n" +
            "                        <h2>Votre demande de changement de mot de passe</h2>\n" +
            "                      Cliquez sur le lien ci-dessous pour en choisir un nouveau.\n" +
            "                        <a href=\"" + link + "\" target=\"_blank\">Changer mon mot de passe</a>\n" +
            "                        <br>The Devoxx Team\n" +
            "                    </body>" +
            "</html>";
        return html;
    }

    public static String getRegisterMailHtml(String nickname) {
        String html = "<html>" +
            "<body> Bienvenue dans cette application Devoxx " + nickname +
            "</body>" +
            "</html>";
        return html;
    }

}
