package fr.esgi.dispatcher.code.service;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CodeAnalyserService {
    public List<String> checkBadCode(String codeFolder) {
        return getRulesViolations(codeFolder);
    }

    private  List<String> getRulesViolations(String codeFolder) {
        String currentPath = "";
        try {

            ////////////////////////
            currentPath = new File("./").getCanonicalPath();
            PMDConfiguration configuration = new PMDConfiguration();
            configuration.setMinimumPriority(RulePriority.MEDIUM);
            configuration.setRuleSets(currentPath + "/javarules.xml");
            configuration.prependClasspath(currentPath + "classes");
            RuleSetFactory ruleSets = RulesetsFactoryUtils.createFactory(configuration);
            RuleContext c = new RuleContext();

            List<DataSource> files = determineFiles(currentPath + "/"+codeFolder);

            Writer rendererOutput = new StringWriter();
            Renderer renderer = createRenderer(rendererOutput);
            renderer.start();

            try {
                PMD.processFiles(configuration, ruleSets, files, c, Collections.singletonList(renderer));
            } finally {
                ClassLoader auxiliaryClassLoader = configuration.getClassLoader();
                if (auxiliaryClassLoader instanceof ClasspathClassLoader) {
                    ((ClasspathClassLoader) auxiliaryClassLoader).close();
                }
            }

            renderer.end();
            renderer.flush();
            Document doc = Jsoup.parse(rendererOutput.toString(), "", Parser.xmlParser());
            System.out.println("Avant la fin codeAnalyserClass");
            return new ArrayList<>(doc.select("violation").eachText());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();

    }

    private static Renderer createRenderer(Writer writer) {
        XMLRenderer xml = new XMLRenderer("UTF-8");
        xml.setWriter(writer);
        return xml;
    }

    private static List<DataSource> determineFiles(String basePath) throws IOException {
        Path dirPath = FileSystems.getDefault().getPath(basePath);
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");

        final List<DataSource> files = new ArrayList<>();

        Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (matcher.matches(path.getFileName())) {
                    //System.out.printf("Using %s%n", path);
                    files.add(new FileDataSource(path.toFile()));
                } else {
                    //System.out.printf("Ignoring %s%n", path);
                }
                return super.visitFile(path, attrs);
            }
        });
        //System.out.printf("Analyzing %d files in %s%n", files.size(), basePath);
        return files;
    }
}
