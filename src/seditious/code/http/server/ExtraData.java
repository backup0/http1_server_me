package seditious.code.http.server;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public class ExtraData {

     public final Map<String, String> postData = new HashMap<>();
     public final List<Path> files = new ArrayList<>();

     public ExtraData(Map<String, String> pd, List<Path> files)
     {
          postData.entrySet().addAll(pd.entrySet());
          this.files.addAll(files);
     }
}
