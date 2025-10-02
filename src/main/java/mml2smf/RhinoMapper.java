/*
 * https://chatgpt.com/c/68dce8ba-d2c8-8323-bf25-294b581565a6
 */

package mml2smf;

import java.lang.reflect.Method;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.annotations.JSSetter;
import vavi.util.Debug;


public class RhinoMapper {

    public static <T> T fromNativeObject(NativeObject obj, Class<T> clazz) {
Debug.print(toJson(obj));
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Method method : clazz.getMethods()) {
                JSSetter setter = method.getAnnotation(JSSetter.class);
                if (setter != null) {
                    String fieldName = getName(setter.value(), method.getName());
//Debug.print(method.getName() + ", " + setter.value() + ", " + fieldName);
                    Object jsValue = obj.get(fieldName, obj);
                    if (jsValue == UniqueTag.NOT_FOUND) {
//Debug.print(fieldName + " not found");
                        continue;
                    }
                    if (jsValue instanceof NativeArray jsArray) {
                        if (jsArray.isEmpty())
                            jsValue = "";
                        else
                            jsValue = String.join("", jsArray);
                    }
                    if (jsValue != null) {
Debug.print(fieldName +  " = " + jsValue);
                        // Convert JS value to Java type if needed
                        Object javaValue = Context.jsToJava(jsValue, method.getParameterTypes()[0]);
                        method.invoke(instance, javaValue);
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            throw new IllegalArgumentException("Mapping failed", e);
        }
    }

    private static String getName(String valueOfAnnotation, String name) {
        if (valueOfAnnotation.isEmpty()) {
            if (name.startsWith("set")) {
                return Character.toLowerCase(name.charAt(3)) + name.substring(4);
            }
        }
        return valueOfAnnotation;
    }

    public static String toJson(NativeObject obj) {
        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();

            scope.put("obj", scope, obj);

            // Use JSON.stringify() in the JS context
            String json = (String) cx.evaluateString(scope, "JSON.stringify(obj)", "jsonify", 1, null);

            return json;
        } finally {
            Context.exit();
        }
    }
}
