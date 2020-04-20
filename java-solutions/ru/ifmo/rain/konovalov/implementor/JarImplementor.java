package ru.ifmo.rain.konovalov.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;

/**
 * Implementation of {@link JarImpler} interface.
 *
 * @author Geny200
 * @see JarImpler
 * @see info.kgeorgiy.java.advanced.implementor.Impler
 * @see ImplerException
 */
public class JarImplementor implements JarImpler {
    /**
     * Constructs a new JarImplementor.
     */
    public JarImplementor() {

    }

    /**
     * Creates an implementation for interfaces depending on input parameters.
     * Use to start:
     * <ul>
     *         <li> {@code java <class_name> -jar <class_to_implement> <jar_file>}
     *         calls {@link JarImplementor#implementJar(Class, Path)}
     *         </li>
     *         <li> {@code java <class_name> <class_to_implement> <path_to_impl>}
     *         calls {@link JarImplementor#implement(Class, Path)}
     *         </li>
     * </ul>
     *
     * @param args array of input parameters ({@link java.lang.String}).
     * @see JarImplementor#implementJar(Class, Path)
     * @see JarImplementor#implement(Class, Path)
     */
    public static void main(String[] args) {
        if (args == null || args.length < 2 || args[0] == null || args[1] == null || args.length >= 3 && args[2] == null) {
            System.out.println("Error: null input argument");
            return;
        }
        JarImplementor implementor = new JarImplementor();
        try {
            if (args.length == 2) {
                implementor.implement(Class.forName(args[0]), Paths.get(args[1]));
            } else {
                if (!args[0].equals("jar")) {
                    System.out.println("Error: '" + args[0] + "' isn't a Implementor command");
                    return;
                }
                implementor.implementJar(Class.forName(args[1]), Paths.get(args[2]));
            }
        } catch (InvalidPathException e) {
            System.out.println("Error: Incorrect path to root: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Incorrect class name: " + e.getMessage());
        } catch (ImplerException e) {
            System.out.println("Implementation Error: " + e.getMessage());
        }
    }

    /**
     * Returns class path.
     *
     * @param token {@link java.lang.Class} the class for which the path is required.
     * @return {@link java.lang.String} - class path
     */
    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        } catch (Exception e) {
            return token.getPackage().getName().replace(".", File.separator);
        }
    }

    /**
     * Returns the full name of the implementation class.
     *
     * @param token {@link java.lang.Class} a class for which the full name of its implementation is required.
     * @return {@link java.lang.String} full name of the implementation class
     */
    private static String getImplName(final Class<?> token) {
        return token.getPackageName() + "." + token.getSimpleName() + "Impl";
    }

    /**
     * Compiles a {@link java.lang.Class} from its implementation. The compiled file is placed along the path <var>root</var>..
     *
     * @param root {@link java.nio.file} for the root folder to compile.
     * @param token compiled class
     */
    public static void compile(final Path root, final Class<?> token) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String[] argsStr = {
                "-encoding",
                "cp866",
                "-cp",
                root + File.pathSeparator + getClassPath(token),
                root.resolve(getImplName(token).replace(".", File.separator) + ".java").toAbsolutePath().toString()
        };
        final int exitCode = compiler.run(null, null, null, argsStr);
    }

    /**
     * Changes to unicode encoding.
     *
     * @param str the {@link java.lang.String} of which you need to change the encoding to unicode
     * @return {@link java.lang.String} - in unicode encoding
     */
    private String toUnicode(String str) {
        StringBuilder escapeBuilder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c >= 128)
                escapeBuilder.append(String.format("\\u%04X", (int) c));
            else
                escapeBuilder.append(c);
        }
        return escapeBuilder.toString();
    }

    /**
     * Creates a list of parameters that can be numbered by passing the corresponding parameter <var>toStr</var>.
     *
     * @param clazz A {@link java.lang.Class} - array of tokens - types.
     * @param toStr A {@link java.util.function} - generates parameter name based on number.
     * @return {@link java.lang.String} representation a list of parameters that can be numbered by passing the corresponding parameter <var>toStr</var>
     */
    private String getStrFromArray(Class<?>[] clazz, Function<Integer, String> toStr) {
        return IntStream.range(0, clazz.length).mapToObj(i -> clazz[i].getCanonicalName() + toStr.apply(i + 1)).collect(Collectors.joining(", "));
    }

    /**
     * Returns the implementation of throws with the type to which the in {@link java.lang.Class[]} belongs.
     *
     * @param clazz A {@link java.lang.Class} - array of tokens - types
     * @return {@link java.lang.String} representation a list of exceptions
     */
    private String getExceptions(Class<?>[] clazz) {
        if (clazz.length == 0)
            return "";
        return " throws " + getStrFromArray(clazz, i -> "");
    }

    /**
     * Returns the implementation of return with the type to which the {@link java.lang.Class} belongs.
     *
     * @param clazz A {@link java.lang.Class} token.
     * @return {@link java.lang.String} - return type
     */
    private String getReturn(Class<?> clazz) {
        if (void.class.equals(clazz))
            return "return";
        else if (boolean.class.equals(clazz))
            return "return false";
        else if (clazz.isPrimitive())
            return "return 0";
        else
            return "return null";
    }

    /**
     * Returns the implementation of {@link java.lang.reflect.Executable} in the form of a {@link java.lang.String}.
     *
     * @param method     A {@link java.lang.reflect.Executable} for which implementation is required.
     * @param name       A {@link java.lang.String} - field name.
     * @param returnName A {@link java.lang.String} - return type name.
     * @param body       A {@link java.lang.String} - function body.
     * @return {@link java.lang.String} - the implementation of {@link java.lang.reflect.Executable}
     */
    private String getStrFromExecutable(Executable method, String name, String returnName, String body) {
        return String.format(
                "\t%s %s %s(%s)%s{ \n\t\t%s;\n\t}\n\n",
                Modifier.toString(method.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT),
                returnName,
                name,
                getStrFromArray(method.getParameterTypes(), i -> " var" + i),
                getExceptions(method.getExceptionTypes()),
                body
        );
    }

    /**
     * Returns the implementation of {@link java.lang.reflect.Method} in the form of a {@link java.lang.String}.
     *
     * @param method A {@link java.lang.reflect.Method} for which implementation is required.
     * @return {@link java.lang.String} - the implementation of {@link java.lang.reflect.Method}
     */
    private String getStrFromMethod(Method method) {
        return getStrFromExecutable(
                method,
                method.getName(),
                method.getReturnType().getCanonicalName(),
                getReturn(method.getReturnType())
        );
    }

    /**
     * Returns the implementation of class constructor in the form of a {@link java.lang.String}.
     *
     * @param className   A {@link java.lang.String} class name.
     * @param constructor A {@link java.lang.reflect.Constructor} class constructor.
     * @return {@link java.lang.String} - representation of constructor
     */
    private String getConstructor(String className, Constructor<?> constructor) {
        if (constructor == null)
            return "";
        return getStrFromExecutable(
                constructor,
                className,
                "",
                String.format("super(%s)",
                        IntStream.range(1, constructor.getParameterCount() + 1)
                                .mapToObj(i -> "var" + i)
                                .collect(Collectors.joining(", "))));
    }

    /**
     * Returns the package of the {@link java.lang.Class} if it exists.
     *
     * @param token A {@link java.lang.Class} token.
     * @return {@link java.lang.String} - full name of the package if it exists
     * @see Class#getPackage()
     */
    private String getPackage(Class<?> token) {
        if (token.getPackage() == null)
            return "";
        return "package " + token.getPackageName() + ";\n";
    }

    /**
     * Returns the implementation of all protected methods in the form of a {@link java.lang.StringBuilder}.
     *
     * @param clazz A {@link java.lang.Class} token that requires the implementation of all protected methods.
     * @return {@link java.lang.StringBuilder} - the implementation of all protected methods
     */
    private StringBuilder getProtectedMethods(Class<?> clazz) {
        if (clazz == null)
            return new StringBuilder();

        StringBuilder result = getProtectedMethods(clazz.getSuperclass());
        for (Method item : clazz.getDeclaredMethods()) {
            int mod = item.getModifiers();
            if (Modifier.isAbstract(mod) && !Modifier.isPublic(mod))
                result.append(getStrFromMethod(item));
        }
        return result;
    }

    /**
     * Returns the implementation of all methods in the form of a {@link java.lang.StringBuilder}.
     *
     * @param clazz A {@link java.lang.Class} token that requires the implementation of all methods.
     * @return {@link java.lang.StringBuilder} - the implementation of all all methods
     */
    private StringBuilder getMethods(Class<?> clazz) {
        StringBuilder result = getProtectedMethods(clazz);
        for (Method item : clazz.getMethods())
            if (Modifier.isAbstract(item.getModifiers()))
                result.append(getStrFromMethod(item));
        return result;
    }

    /**
     * Produces code implementing class or interface specified by provided <var>token</var>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <var>Impl</var> suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * <var>root</var> directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to <var>$root/java/util/ListImpl.java</var>
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be
     *                                                                 generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (token == null || root == null)
            throw new ImplerException("Null arguments");
        else if (Enum.class.equals(token))
            throw new ImplerException("Can't implement enum");
        else if (token.isPrimitive())
            throw new ImplerException("Can't implement primitive type");
        else if (token.isArray())
            throw new ImplerException("Can't implement array");
        else if (Modifier.isFinal(token.getModifiers()))
            throw new ImplerException("Can't extend final");
        else if (Modifier.isPrivate(token.getModifiers()))
            throw new ImplerException("Can't extend private");

        if (token.getPackage() != null) {
            root = root.resolve(token.getPackageName().replace(".", File.separator));
        }
        Path javaFile = root.resolve(token.getSimpleName() + "Impl.java");

        if (!Files.exists(javaFile))
            try {
                Files.createDirectories(root);
                Files.createFile(javaFile);
            } catch (IOException e) {
                throw new ImplerException("Can't create java file" + e.getMessage());
            }

        Constructor<?> constructor = null;
        if (!token.isInterface()) {
            for (Constructor<?> item : token.getDeclaredConstructors())
                if (!Modifier.isPrivate(item.getModifiers())) {
                    constructor = item;
                    break;
                }
            if (constructor == null)
                throw new ImplerException("Can't created private constructor");
        }

        try (BufferedWriter os = Files.newBufferedWriter(javaFile)) {
            os.write(toUnicode(
                    String.format(
                            "%s\npublic class %sImpl %s %s {" + System.lineSeparator() + "%s%s};",
                            getPackage(token),
                            token.getSimpleName(),
                            token.isInterface() ? "implements" : "extends",
                            token.getCanonicalName(),
                            getConstructor(token.getSimpleName() + "Impl", constructor),
                            getMethods(token)
                    )));
        } catch (IOException e) {
            System.err.println("An OutPut error occurred:" + e.getMessage());
        }
    }

    /**
     * Produces <var>.jar</var> file implementing class or interface specified by provided <var>token</var>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <var>Impl</var> suffix
     * added.
     *
     * @param token   type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path temp = Paths.get("temp"), deleteTemp = temp;
        if (token.getPackage() != null) {
            deleteTemp = temp.resolve(token.getPackageName().replace(".", File.separator));
        }
        Path compilePath = temp.resolve(getImplName(token).replace(".", File.separator) + ".class");
        String className = getImplName(token).replace(".", "/") + ".class";

        LinkedList<File> deleteList = new LinkedList<>();
        deleteList.add(compilePath.toFile());
        deleteList.add(deleteTemp.resolve(token.getSimpleName() + "Impl.java").toFile());

        while (deleteTemp != null && !deleteTemp.toString().isEmpty()) {
            deleteList.add(deleteTemp.toFile());
            deleteTemp = deleteTemp.getParent();
        }

        for (Iterator<File> iterator = deleteList.descendingIterator(); iterator.hasNext(); )
            iterator.next().deleteOnExit();

        implement(token, temp);
        compile(temp, token);

        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.IMPLEMENTATION_VENDOR, "Geny200");

        try (JarOutputStream os = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            os.putNextEntry(new ZipEntry(className));
            Files.copy(compilePath, os);
        } catch (IOException e) {
            throw new ImplerException("couldn't write to jar file");
        }
    }
}
