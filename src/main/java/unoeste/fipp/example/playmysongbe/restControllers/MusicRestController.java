package unoeste.fipp.example.playmysongbe.restControllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import unoeste.fipp.example.playmysongbe.entities.Erro;
import unoeste.fipp.example.playmysongbe.entities.Music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping(value = "apis") // define um endpoint
public class MusicRestController
{
    @Autowired // servidor que instacia
    private HttpServletRequest request;
    @PostMapping(value = "add-music")
    public ResponseEntity<Object> addUsuario(@RequestParam("foto") MultipartFile file,
                                             @RequestParam("titulo") String titulo,
                                             @RequestParam("artista") String artista,
                                             @RequestParam("estilo") String estilo)
    {
        final String UPLOAD_FOLDER = "src/main/resources/static/uploads/"; // define a onde musicas serão armazenadas
        //titulo_estilo_artista
        String novoNome;
        novoNome = titulo + "_" + estilo + "_" + artista + ".mp3";
        novoNome = novoNome.replace(" ", "");
        try {
            //cria uma pasta na área estática para acomodar os arquivos recebidos, caso não exista
            File uploadFolder = new File(UPLOAD_FOLDER);
            if (!uploadFolder.exists()) uploadFolder.mkdir();
            //criar um nome para o arquivo, para isso concatenar artista, titulo e estilo
            file.transferTo(new File(uploadFolder.getAbsolutePath() + "\\"+novoNome));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Erro("Erro ao armazenar o arquivo. " + e.getMessage()));
        }
        return ResponseEntity.ok(new Music(titulo, artista, estilo, novoNome));
    }
    @GetMapping(value = "find-musics/{filtro}")
    public ResponseEntity<Object> findMusics(@PathVariable (value = "filtro") String filtro) // colocar parametros depois
    {
        File uploadFolder = new File("src/main/resources/static/uploads/");
        String[] files = uploadFolder.list();

        // caso a lista estiver vazia retorne um erro

        if (files.length > 0)
        {
            List<Music> musicList = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {

                String musica = files[i];
                char flag = '0', flag2 = '0', flag3 = '0';
                String titulo = "", artista = "", estilo = "";

                for (int j = 0; j < musica.length(); j++) {
                    if (musica.charAt(j) != '_' && flag != '1')
                    {
                        titulo += musica.charAt(j);
                    } else if (musica.charAt(j) == '_' && flag != '1')
                    {
                        flag = '1';
                    } else if (musica.charAt(j) != '_' && flag2 != '1' && flag == '1')
                    {
                        estilo += musica.charAt(j);
                    } else if (musica.charAt(j) == '_' && flag2 != '1' && flag == '1')
                    {
                        flag2 = '1';
                    } else if (musica.charAt(j) != '.' && flag3 != '1' && flag == '1' && flag2 == '1')
                    {
                        artista += musica.charAt(j);
                    } else if (musica.charAt(j) == '.' && flag3 != '1' && flag == '1' && flag2 == '1')
                    {
                        flag3 = '1';
                    }

                }
                if (titulo.equalsIgnoreCase(filtro))
                {
                    musicList.add(new Music(titulo, artista, estilo, getHostStatic()+musica));
                }
                else if (artista.equalsIgnoreCase(filtro)){
                    musicList.add(new Music(titulo, artista, estilo, getHostStatic()+musica));
                }
                else if (estilo.equalsIgnoreCase(filtro)){
                    musicList.add(new Music(titulo, artista, estilo, getHostStatic()+musica));
                }
            }
            if (musicList.size() > 0)
                return ResponseEntity.ok(musicList); // envia um vetor de strings
            else
                return ResponseEntity.badRequest().body(new Erro(filtro + " não foi encontrado"));
        }
        else
            return ResponseEntity.badRequest().body(new Erro("Não Há Músicas Para Serem Consultadas"));
    }
    @GetMapping(value = "find-musics")
    public ResponseEntity<Object> findMusics()
    {
        File uploadFolder = new File("src/main/resources/static/uploads/");
        String[] files = uploadFolder.list();
        // caso a lista estiver vazia retorne um erro
        if (files.length > 0)
        {
            List<Music> musicList = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {

                String musica = files[i];
                char flag = '0', flag2 = '0', flag3 = '0';

                String titulo = "", artista = "", estilo = "";
                for (int j = 0; j < musica.length(); j++) {
                    if (musica.charAt(j) != '_' && flag != '1')
                    {
                        titulo += musica.charAt(j);
                    } else if (musica.charAt(j) == '_' && flag != '1')
                    {
                        flag = '1';
                    } else if (musica.charAt(j) != '_' && flag2 != '1' && flag == '1')
                    {
                        estilo += musica.charAt(j);
                    } else if (musica.charAt(j) == '_' && flag2 != '1' && flag == '1')
                    {
                        flag2 = '1';
                    } else if (musica.charAt(j) != '.' && flag3 != '1' && flag == '1' && flag2 == '1')
                    {
                        artista += musica.charAt(j);
                    } else if (musica.charAt(j) == '.' && flag3 != '1' && flag == '1' && flag2 == '1')
                    {
                        flag3 = '1';
                    }

                }
                musicList.add(new Music(titulo, artista, estilo, getHostStatic()+musica));
            }
            return ResponseEntity.ok(musicList);
        }
        else
            return ResponseEntity.badRequest().body(new Erro("Não Há Músicas Para Serem Consultadas"));

    }

    public String getHostStatic() {
        return "http://"+request.getServerName().toString()+":"+request.getServerPort()+"/uploads/";
    }
}
