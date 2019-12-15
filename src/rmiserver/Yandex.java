package rmiserver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Yandex {

    private  String API_KEY = "trnsl.1.1.20191212T215632Z.3fd91cc28e7dea03.97e308038f55eb181c8bee5fa57f2138e784cdec";

    public  String request(String x) throws IOException {

        URL url = new URL(x);
        URLConnection con = url.openConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String received = in.readLine();
        in.close();
        return received;

    }

    public  Map<String, String> getLinguas() throws IOException {
        String langs = request("https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=" + API_KEY + "&ui=en");
        JSONObject obj = (JSONObject) JSONValue.parse(langs);

        String text = obj.get("langs").toString();
        String novo = text.substring(1,text.length()-1);

        String[] auxiliar = novo.split(",");

        Map<String, String> allLanguages = new HashMap<String, String>();

        for (String s : auxiliar) {
            String[] x = s.split(":");

            String letras = x[0].substring(1, x[0].length()-1);
            String nome = x[1].substring(1, x[1].length()-1);

            allLanguages.put(letras, nome);
        }
        return allLanguages;
    }

    public  String translate(String texto,String linguagemBase,String linguagemDestino) throws IOException {
        String url = ("https://translate.yandex.net/api/v1.5/tr.json/translate?key="+API_KEY+"&text="+URLEncoder.encode(texto,"UTF-8")+"&lang=" + linguagemBase + "-" + linguagemDestino) ;
        String novoTexto = request(url);
        JSONObject obj = (JSONObject) JSONValue.parse(novoTexto);
        JSONArray array = (JSONArray) obj.get("text");
        String text = array.toString();
        return text.substring(1,text.length()-1);
    }

    public  String detect(String texto) throws IOException {
        String url = ("https://translate.yandex.net/api/v1.5/tr.json/detect?key="+API_KEY+"&text="+ URLEncoder.encode(texto,"UTF-8"));
        String novoTexto = request(url);
        JSONObject obj = (JSONObject) JSONValue.parse(novoTexto);
        System.out.println(obj.get("lang"));
        return (String)obj.get("lang");
    }

    public  String getTraducao(Map<String, String> mapalinguas, String linguaTraduzir) {
        for (String letras : mapalinguas.keySet()) {
            if (mapalinguas.get(letras).equalsIgnoreCase(linguaTraduzir)) {
                return letras;
            }
        }
        return null;
    }
}
