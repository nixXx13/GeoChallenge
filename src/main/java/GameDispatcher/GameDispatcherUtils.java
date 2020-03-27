package GameDispatcher;

import java.util.List;
import java.util.stream.Collectors;

public class GameDispatcherUtils {
    public static boolean isStringInList(List<String> list, String element){
        List<String> ret = list.stream().filter(el->el.equals(element)).collect(Collectors.toList());
        return !ret.isEmpty();
    }
}
