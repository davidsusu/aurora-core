package hu.webarticum.aurora.core.io.bundled;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hu.webarticum.aurora.core.io.AbstractDocumentIo;
import hu.webarticum.aurora.core.model.Activity;
import hu.webarticum.aurora.core.model.Aspect;
import hu.webarticum.aurora.core.model.Block;
import hu.webarticum.aurora.core.model.Board;
import hu.webarticum.aurora.core.model.Color;
import hu.webarticum.aurora.core.model.Document;
import hu.webarticum.aurora.core.model.Labeled;
import hu.webarticum.aurora.core.model.Period;
import hu.webarticum.aurora.core.model.Resource;
import hu.webarticum.aurora.core.model.ResourceSubset;
import hu.webarticum.aurora.core.model.Store;
import hu.webarticum.aurora.core.model.Tag;
import hu.webarticum.aurora.core.model.TimingSet;
import hu.webarticum.aurora.core.model.Value;
import hu.webarticum.aurora.core.model.time.CustomTimeLimit;
import hu.webarticum.aurora.core.model.time.Time;
import hu.webarticum.aurora.core.model.time.TimeLimit;
import hu.webarticum.aurora.core.text.CoreText;


public class XmlDocumentIo extends AbstractDocumentIo {

    private static final long serialVersionUID = 1L;

    
    public static final String NAMESPACE = "https://orarend-program.hu/xml/schema/document";
    
    public static final String SCHEMA_LOCATION = "https://orarend-program.hu/xml/schema/document/document.xsd";
    

    public XmlDocumentIo() {
        super(CoreText.get("io.xml.name", "XML format"), "xml");
    }
    
    
    @Override
    public void save(Document document, OutputStream outputStream) throws IOException {
        XmlBuilder documentElementBuilder = new XmlBuilder(document);

        org.w3c.dom.Document xmlDocument = documentElementBuilder.build();
        
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(xmlDocument), new StreamResult(outputStream));
        } catch (TransformerException e) {
            throw new IOException("Transformer exception", e);
        }
    }

    @Override
    public Document load(InputStream inputStream) throws IOException, ParseException {
        DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance(); // NOSONAR protected below
        xmlFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        xmlFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        org.w3c.dom.Document xmlDocument;
        try {
            xmlDocument = xmlFactory.newDocumentBuilder().parse(inputStream);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
        return new XmlProcessor(xmlDocument).process();
    }
    
    
    private class XmlBuilder {
        
        org.w3c.dom.Document xmlDocument;
        
        final Document document;
        
        Labeled.LabeledStore<Period> periodStore;

        Labeled.LabeledStore<TimingSet> timingSetStore;

        Labeled.LabeledStore<Tag> tagStore;

        Labeled.LabeledStore<Resource> resourceStore;

        Labeled.LabeledStore<Block> blockStore;

        Labeled.LabeledStore<Board> boardStore;
        
        Map<Resource, Map<Resource.Splitting.Part, String>> resourceSplittingPartNameMap;
        
        
        public XmlBuilder(Document document) {
            this.document = document;
        }
        
        
        public org.w3c.dom.Document build() throws IOException {
            periodStore = normalizeLabeledStore(document.getPeriodStore());
            timingSetStore = normalizeLabeledStore(document.getTimingSetStore());
            tagStore = normalizeLabeledStore(document.getTagStore());
            resourceStore = normalizeLabeledStore(document.getResourceStore());
            blockStore = normalizeLabeledStore(document.getBlockStore());
            boardStore = normalizeLabeledStore(document.getBoardStore());
            
            resourceSplittingPartNameMap = new HashMap<Resource, Map<Resource.Splitting.Part, String>>();
            
            DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance(); // NOSONAR protected below
            xmlFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            xmlFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            
            try {
                xmlDocument = xmlFactory.newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e) {
                throw new IOException("Can not initialize document");
            }
            
            Element documentElement = createDocumentElement();
            
            xmlDocument.appendChild(documentElement);
            
            return xmlDocument;
        }
        
        private <L extends Labeled> Labeled.LabeledStore<L> normalizeLabeledStore(Labeled.LabeledStore<L> store) {
            Labeled.LabeledStore<L> result = new Labeled.LabeledStore<L>();
            result.registerAll(store);
            return result;
        }
        
        private Element createDocumentElement() {
            Element documentElement = xmlDocument.createElementNS(NAMESPACE, "document");
            
            // XXX
            documentElement.setAttribute("xmlns", NAMESPACE);
            documentElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            documentElement.setAttribute("xsi:schemaLocation", NAMESPACE + " " + SCHEMA_LOCATION);
            
            Element documentHeaderElement = xmlDocument.createElement("header");
            documentElement.appendChild(documentHeaderElement);
            
            documentHeaderElement.appendChild(createDocumentInfoElement("label", document.getLabel()));

            Element documentPeriodsElement = xmlDocument.createElement("periods");
            documentElement.appendChild(documentPeriodsElement);
            for (Map.Entry<String, Period> periodEntry: periodStore.entries()) {
                String periodId = periodEntry.getKey();
                Period period = periodEntry.getValue();
                Element periodElement = createPeriodElement(period, periodId);
                documentPeriodsElement.appendChild(periodElement);
            }

            Element documentTimingSetsElement = xmlDocument.createElement("timingSets");
            documentElement.appendChild(documentTimingSetsElement);
            for (Map.Entry<String, TimingSet> timingSetEntry: timingSetStore.entries()) {
                String timingSetId = timingSetEntry.getKey();
                TimingSet timingSet = timingSetEntry.getValue();
                Element timingSetElement = createTimingSetElement(timingSet, timingSetId);
                documentTimingSetsElement.appendChild(timingSetElement);
            }

            Element documentTagsElement = xmlDocument.createElement("tags");
            for (Map.Entry<String, Tag> tagEntry: tagStore.entries()) {
                String tagId = tagEntry.getKey();
                Tag tag = tagEntry.getValue();
                Element tagElement = createTagElement(tag, tagId);
                documentTagsElement.appendChild(tagElement);
            }
            documentElement.appendChild(documentTagsElement);

            Element documentResourcesElement = xmlDocument.createElement("resources");
            documentElement.appendChild(documentResourcesElement);
            for (Map.Entry<String, Resource> resourceEntry: resourceStore.entries()) {
                String resourceId = resourceEntry.getKey();
                Resource resource = resourceEntry.getValue();
                Element documentResourceElement = createResourceElement(resource, resourceId);
                documentResourcesElement.appendChild(documentResourceElement);
            }
            documentElement.appendChild(documentResourcesElement);
            
            Element documentBlocksElement = xmlDocument.createElement("blocks");
            for (Map.Entry<String, Block> blockEntry: blockStore.entries()) {
                String blockId = blockEntry.getKey();
                Block block = blockEntry.getValue();
                Element blockElement = createBlockElement(block, blockId);
                documentBlocksElement.appendChild(blockElement);
            }
            documentElement.appendChild(documentBlocksElement);
            
            Element documentBoardsElement = xmlDocument.createElement("boards");
            for (Map.Entry<String, Board> boardEntry: boardStore.entries()) {
                String boardId = boardEntry.getKey();
                Board board = boardEntry.getValue();
                Element boardElement = createBoardElement(board, boardId);
                documentBoardsElement.appendChild(boardElement);
            }
            documentElement.appendChild(documentBoardsElement);
            
            Value extraData = document.getExtraData();
            if (!extraData.isNull()) {
                Element extraDataElement = xmlDocument.createElement("extraData");
                Element valueElement = createValueElement(extraData);
                extraDataElement.appendChild(valueElement);
                documentElement.appendChild(extraDataElement);
            }
            
            return documentElement;
        }

        protected Element createDocumentInfoElement(String name, String value) {
            Element documentInfoElement = xmlDocument.createElement("info");
            documentInfoElement.setAttribute("name", name);
            documentInfoElement.setAttribute("value", value);
            return documentInfoElement;
        }
        
        protected Element createPeriodElement(Period period) {
            return createPeriodElement(period, null);
        }
        
        protected Element createPeriodElement(Period period, String id) {
            Element periodElement = xmlDocument.createElement("period");
            if (id != null) {
                periodElement.setAttribute("id", id);
            }
            periodElement.setAttribute("label", period.getLabel());
            int term = period.getTerm();
            int position = period.getPosition();
            if (term != 0 && position != 0) {
                periodElement.setAttribute("term", "" + term);
                periodElement.setAttribute("position", "" + position);
            }
            return periodElement;
        }

        protected Element createTagElement(Tag tag) {
            return createTagElement(tag, null);
        }
        
        protected Element createTagElement(Tag tag, String id) {
            Element tagElement = createAspectElement(tag, "tag", id);
            tagElement.setAttribute("type", tag.getType().name().toLowerCase());
            return tagElement;
        }

        protected Element createResourceElement(Resource resource) {
            return createResourceElement(resource, null);
        }
        
        protected Element createResourceElement(Resource resource, String id) {
            Element resourceElement = createAspectElement(resource, "resource", id);
            resourceElement.setAttribute("type", resource.getType().name().toLowerCase());
            resourceElement.setAttribute("quantity", ""+resource.getQuantity());
            String email = resource.getEmail();
            if (!email.isEmpty()) {
                resourceElement.setAttribute("email", email);
            }
            Resource.SplittingManager splittingManager = resource.getSplittingManager();
            List<Resource.Splitting> splittings = splittingManager.getSplittings();
            if (!splittings.isEmpty()) {
                Map<Resource.Splitting.Part, String> splittingPartNameMap = new HashMap<Resource.Splitting.Part, String>();
                Element splittingsElement = xmlDocument.createElement("splittings");
                Labeled.LabeledStore<Resource.Splitting> splittingStore = new Labeled.LabeledStore<Resource.Splitting>();
                for (Resource.Splitting splitting: splittings) {
                    Element splittingElement = xmlDocument.createElement("splitting");
                    splittingElement.setAttribute("label", splitting.getLabel());
                    String splittingName = splittingStore.register(splitting, Store.REGISTER_MODE.INSERT_AUTO);
                    splittingElement.setAttribute("name", splittingName);
                    Labeled.LabeledStore<Resource.Splitting.Part> splittingPartStore = new Labeled.LabeledStore<Resource.Splitting.Part>();
                    List<Resource.Splitting.Part> splittingParts = splitting.getParts();
                    for (Resource.Splitting.Part splittingPart: splittingParts) {
                        Element splittingPartElement = xmlDocument.createElement("part");
                        splittingPartElement.setAttribute("label", splittingPart.getLabel());
                        String splittingPartName = splittingPartStore.register(splittingPart, Store.REGISTER_MODE.INSERT_AUTO);
                        splittingPartElement.setAttribute("name", splittingPartName);
                        splittingElement.appendChild(splittingPartElement);
                        splittingPartNameMap.put(splittingPart, splittingName+"."+splittingPartName);
                    }
                    splittingsElement.appendChild(splittingElement);
                }
                resourceSplittingPartNameMap.put(resource, splittingPartNameMap);
                resourceElement.appendChild(splittingsElement);
            }
            return resourceElement;
        }

        protected Element createAspectElement(Aspect aspect, String tagName, String id) {
            Element aspectElement = xmlDocument.createElement(tagName);
            if (id!=null) {
                aspectElement.setAttribute("id", id);
            }
            aspectElement.setAttribute("label", aspect.getLabel());
            String acronym = aspect.getAcronym();
            if (!acronym.isEmpty()) {
                aspectElement.setAttribute("acronym", acronym);
            }
            aspectElement.setAttribute("color", aspect.getColor().getHexa());
            aspectElement.setAttribute("timingSetEnabled", aspect.isTimingSetEnabled()?"1":"0");
            aspectElement.setAttribute("timeLimitEnabled", aspect.isTimeLimitEnabled()?"1":"0");
            
            Element aspectTimingSetsElement = xmlDocument.createElement("timingSets");
            Aspect.TimingSetManager aspectTimingSetManager = aspect.getTimingSetManager();
            TimingSet aspectDefaultTimingSet = aspectTimingSetManager.getDefaultTimingSet();
            if (aspectDefaultTimingSet!=null) {
                Element aspectDefaultTimingSetElement = xmlDocument.createElement("timingSetRef");
                String aspectDefaultTimingSetId = timingSetStore.register(aspectDefaultTimingSet, Store.REGISTER_MODE.INSERT_AUTO);
                aspectDefaultTimingSetElement.setAttribute("timingSetId", aspectDefaultTimingSetId);
                aspectTimingSetsElement.appendChild(aspectDefaultTimingSetElement);
            }
            Map<Period, TimingSet> aspectPeriodTimingSets = aspectTimingSetManager.getPeriodTimingSets();
            for (Map.Entry<Period, TimingSet> entry: aspectPeriodTimingSets.entrySet()) {
                Element aspectPeriodTimingSetElement = xmlDocument.createElement("timingSetRef");
                
                // FIXME: ad hoc?
                String aspectPeriodTimingSetId = timingSetStore.register(entry.getValue(), Store.REGISTER_MODE.INSERT_AUTO);
                String aspectPeriodTimingSetPeriodId = periodStore.register(entry.getKey(), Store.REGISTER_MODE.INSERT_AUTO);
                aspectPeriodTimingSetElement.setAttribute("timingSetId", aspectPeriodTimingSetId);
                aspectPeriodTimingSetElement.setAttribute("periodId", aspectPeriodTimingSetPeriodId);
                
                
                aspectTimingSetsElement.appendChild(aspectPeriodTimingSetElement);
            }
            if (aspectDefaultTimingSet!=null || !aspectPeriodTimingSets.isEmpty()) {
                aspectElement.appendChild(aspectTimingSetsElement);
            }

            Element aspectTimeLimitsElement = xmlDocument.createElement("timeLimits");
            Aspect.TimeLimitManager aspectTimeLimitManager = aspect.getTimeLimitManager();
            TimeLimit aspectDefaultTimeLimit = aspectTimeLimitManager.getDefaultTimeLimit();
            if (aspectDefaultTimeLimit!=null) {
                Element aspectDefaultTimeLimitElement = createTimeLimitElement(aspectDefaultTimeLimit, null);
                aspectTimeLimitsElement.appendChild(aspectDefaultTimeLimitElement);
            }
            Map<Period, TimeLimit> aspectPeriodTimeLimits = aspectTimeLimitManager.getPeriodTimeLimits();
            for (Map.Entry<Period, TimeLimit> entry: aspectPeriodTimeLimits.entrySet()) {
                Element aspectPeriodTimeLimitElement = createTimeLimitElement(aspectDefaultTimeLimit, entry.getKey());
                aspectTimeLimitsElement.appendChild(aspectPeriodTimeLimitElement);
            }
            if (!aspectPeriodTimeLimits.isEmpty() ||
                (
                    aspectDefaultTimeLimit!=null
                    && (aspect.isTimeLimitEnabled() || !aspectDefaultTimeLimit.isAlways())
                )
            ) {
                aspectElement.appendChild(aspectTimeLimitsElement);
            }
            
            return aspectElement;
        }
        
        protected Element createTimingSetElement(TimingSet timingSet) {
            return createTimingSetElement(timingSet, null);
        }
        
        protected Element createTimingSetElement(TimingSet timingSet, String id) {
            Element timingSetElement = xmlDocument.createElement("timingSet");
            if (id!=null) {
                timingSetElement.setAttribute("id", id);
            }
            timingSetElement.setAttribute("label", timingSet.getLabel());
            for (TimingSet.TimeEntry timeEntry: timingSet) {
                Element timeEntryElement = xmlDocument.createElement("entry");
                timeEntryElement.setAttribute("time", timeEntry.getTime().toString());
                timeEntryElement.setAttribute("label", timeEntry.getLabel());
                timingSetElement.appendChild(timeEntryElement);
            }
            return timingSetElement;
        }
        
        protected Element createTimeLimitElement(TimeLimit timeLimit) {
            return createTimeLimitElement(timeLimit, null);
        }
        
        protected Element createTimeLimitElement(TimeLimit timeLimit, Period period) {
            Element timeLimitElement = xmlDocument.createElement("timeLimit");
            if (period!=null) {
                timeLimitElement.setAttribute("period", periodStore.register(period, Store.REGISTER_MODE.INSERT_AUTO));
            }
            // FIXME: dedicated elements?
            CustomTimeLimit customTimeLimit = new CustomTimeLimit(timeLimit);
            timeLimitElement.setAttribute("type", "custom");
            timeLimitElement.setAttribute("startState", customTimeLimit.getStartState()?"1":"0");
            for (Time time: customTimeLimit.getTimes()) {
                Element switchElement = xmlDocument.createElement("switch");
                switchElement.setAttribute("time", time.toString());
                timeLimitElement.appendChild(switchElement);
            }
            return timeLimitElement;
        }

        protected Element createBlockElement(Block block) {
            return createBlockElement(block, null);
        }
        
        protected Element createBlockElement(Block block, String id) {
            Element blockElement = xmlDocument.createElement("block");
            if (id!=null) {
                blockElement.setAttribute("id", id);
            }
            blockElement.setAttribute("label", block.getLabel());
            blockElement.setAttribute("length", Time.formatSeconds(block.getLength()));
            Block.ActivityManager activityManager = block.getActivityManager();
            for (Block.ActivityManager.ActivityEntry entry: activityManager) {
                blockElement.appendChild(createActivityElement(entry.getActivity(), entry.getPeriods()));
            }
            return blockElement;
        }
        
        protected Element createActivityElement(Activity activity, Collection<Period> periods) {
            
            // FIXME: unreferenced...
            
            Element activityElement = xmlDocument.createElement("activity");
            activityElement.setAttribute("label", activity.getLabel());
            for (Period period: periods) {
                Element activityPeriodElement = xmlDocument.createElement("periodRef");
                activityPeriodElement.setAttribute("periodId", periodStore.register(period, Store.REGISTER_MODE.INSERT_AUTO));
                activityElement.appendChild(activityPeriodElement);
            }
            Activity.TagManager activityTagManager = activity.getTagManager();
            for (Tag tag: activityTagManager.getTags()) {
                Element activityTagElement = xmlDocument.createElement("tagRef");
                activityTagElement.setAttribute("tagId", tagStore.register(tag, Store.REGISTER_MODE.INSERT_AUTO));
                activityElement.appendChild(activityTagElement);
            }
            Activity.ResourceManager activityResourceManager = activity.getResourceManager();
            for (ResourceSubset resourceSubset: activityResourceManager.getResourceSubsets()) {
                Element activityResourceElement = xmlDocument.createElement("resourceRef");
                Resource resource = resourceSubset.getResource();
                activityResourceElement.setAttribute("resourceId", resourceStore.register(resource, Store.REGISTER_MODE.INSERT_AUTO));
                if (!(resourceSubset instanceof ResourceSubset.Whole)) {
                    Element activityResourceSubsetSetElement = createResourceSubsetElement(resourceSubset);
                    activityResourceElement.appendChild(activityResourceSubsetSetElement);
                }
                activityElement.appendChild(activityResourceElement);
            }
            return activityElement;
        }

        protected Element createResourceSubsetElement(ResourceSubset resourceSubset) {
            Element resourceSubsetElement = xmlDocument.createElement("subset");
            Resource resource = resourceSubset.getResource();
            if (resourceSubset instanceof ResourceSubset.SplittingPart) {
                ResourceSubset.SplittingPart splittingPartResourceSubset = (ResourceSubset.SplittingPart)resourceSubset;
                Resource.Splitting.Part splittingPart = splittingPartResourceSubset.getSplittingPart();
                resourceSubsetElement.setAttribute("type", "part");
                try {
                    resourceSubsetElement.setAttribute("partName", resourceSplittingPartNameMap.get(resource).get(splittingPart));
                } catch (NullPointerException e) {
                }
            } else if (resourceSubset instanceof ResourceSubset.Union) {
                resourceSubsetElement.setAttribute("type", "union");
            } else if (resourceSubset instanceof ResourceSubset.Intersection) {
                resourceSubsetElement.setAttribute("type", "intersection");
            } else if (resourceSubset instanceof ResourceSubset.Difference) {
                resourceSubsetElement.setAttribute("type", "difference");
            } else if (resourceSubset instanceof ResourceSubset.SymmetricDifference) {
                resourceSubsetElement.setAttribute("type", "symmetricDifference");
            } else if (resourceSubset instanceof ResourceSubset.Inverse) {
                resourceSubsetElement.setAttribute("type", "inverse");
            } else if (resourceSubset instanceof ResourceSubset.Whole) {
                resourceSubsetElement.setAttribute("type", "whole");
            }
            for (ResourceSubset childResourceSubset: resourceSubset.getChildren()) {
                resourceSubsetElement.appendChild(createResourceSubsetElement(childResourceSubset));
            }
            return resourceSubsetElement;
        }
        
        protected Element createBoardElement(Board board, String id) {
            Element boardElement = xmlDocument.createElement("board");
            boardElement.setAttribute("id", id);
            boardElement.setAttribute("label", board.getLabel());
            for (Board.Entry entry: board) {
                Element boardEntryElement = xmlDocument.createElement("entry");
                boardEntryElement.setAttribute("time", entry.getTime().toString());
                boardEntryElement.setAttribute("blockId", blockStore.register(entry.getBlock(), Store.REGISTER_MODE.INSERT_AUTO));
                boardElement.appendChild(boardEntryElement);
            }
            return boardElement;
        }
        
        protected Element createValueElement(Value value) {
            Element resultElement;
            Value.Type valueType = value.getType();
            switch (valueType) {
                case BOOLEAN:
                    resultElement = xmlDocument.createElement("boolean");
                    resultElement.setAttribute("value", value.getAsBoolean()?"1":"0");
                    break;
                case BYTE:
                    resultElement = xmlDocument.createElement("byte");
                    resultElement.setAttribute("value", ""+value.getAsByte());
                    break;
                case SHORT:
                    resultElement = xmlDocument.createElement("short");
                    resultElement.setAttribute("value", ""+value.getAsShort());
                    break;
                case INT:
                    resultElement = xmlDocument.createElement("int");
                    resultElement.setAttribute("value", ""+value.getAsInt());
                    break;
                case LONG:
                    resultElement = xmlDocument.createElement("long");
                    resultElement.setAttribute("value", ""+value.getAsLong());
                    break;
                case FLOAT:
                    // FIXME: precision?
                    resultElement = xmlDocument.createElement("float");
                    resultElement.setAttribute("value", ""+value.getAsFloat());
                    break;
                case DOUBLE:
                    // FIXME: precision?
                    resultElement = xmlDocument.createElement("double");
                    resultElement.setAttribute("value", ""+value.getAsDouble());
                    break;
                case CHAR:
                    // TODO: handle null character
                    resultElement = xmlDocument.createElement("char");
                    resultElement.setAttribute("value", ""+value.getAsChar());
                    break;
                case STRING:
                    // TODO: handle null character
                    resultElement = xmlDocument.createElement("string");
                    resultElement.setAttribute("value", value.getAsString());
                    break;
                case MAP:
                    resultElement = xmlDocument.createElement("map");
                    for (Map.Entry<Value, Value> entry: value.getAsMap().entrySet()) {
                        Element entryElement = xmlDocument.createElement("entry");
                        Element keyElement = xmlDocument.createElement("key");
                        keyElement.appendChild(createValueElement(entry.getKey()));
                        entryElement.appendChild(keyElement);
                        Element valueElement = xmlDocument.createElement("value");
                        valueElement.appendChild(createValueElement(entry.getValue()));
                        entryElement.appendChild(valueElement);
                        resultElement.appendChild(entryElement);
                    }
                    break;
                case LIST:
                    resultElement = xmlDocument.createElement("list");
                    for (Value item: value.getAsList()) {
                        resultElement.appendChild(createValueElement(item));
                    }
                    break;
                case SET:
                    resultElement = xmlDocument.createElement("set");
                    for (Value item: value.getAsSet()) {
                        resultElement.appendChild(createValueElement(item));
                    }
                    break;
                case PERIOD:
                    Period period = value.getAsPeriod();
                    String periodId = periodStore.register(period, Store.REGISTER_MODE.INSERT_STRICT);
                    if (periodId==null) {
                        resultElement = createPeriodElement(period);
                    } else {
                        resultElement = xmlDocument.createElement("periodRef");
                        resultElement.setAttribute("periodId", periodId);
                    }
                    break;
                case TIMINGSET:
                    TimingSet timingSet = value.getAsTimingSet();
                    String timingSetId = timingSetStore.register(timingSet, Store.REGISTER_MODE.INSERT_STRICT);
                    if (timingSetId==null) {
                        resultElement = createTimingSetElement(timingSet);
                    } else {
                        resultElement = xmlDocument.createElement("timingSetRef");
                        resultElement.setAttribute("timingSetId", timingSetId);
                    }
                    break;
                case TIMELIMIT:
                    TimeLimit timeLimit = value.getAsTimeLimit();
                    resultElement = createTimeLimitElement(timeLimit);
                    break;
                case TAG:
                    Tag tag = value.getAsTag();
                    String tagId = tagStore.register(tag, Store.REGISTER_MODE.INSERT_STRICT);
                    if (tagId==null) {
                        resultElement = createTagElement(tag);
                    } else {
                        resultElement = xmlDocument.createElement("tagRef");
                        resultElement.setAttribute("tagId", tagId);
                    }
                    break;
                case RESOURCE:
                    Resource resource = value.getAsResource();
                    String resourceId = resourceStore.register(resource, Store.REGISTER_MODE.INSERT_STRICT);
                    if (resourceId==null) {
                        resultElement = createResourceElement(resource);
                    } else {
                        resultElement = xmlDocument.createElement("resourceRef");
                        resultElement.setAttribute("resourceId", resourceId);
                    }
                    break;
                case RESOURCESUBSET:
                    ResourceSubset resourceSubset = value.getAsResourceSubset();
                    Resource resourceSubsetResource = resourceSubset.getResource();
                    String resourceSubsetResourceId = resourceStore.register(resourceSubsetResource, Store.REGISTER_MODE.INSERT_STRICT);
                    if (resourceSubsetResourceId==null) {
                        resultElement = createResourceElement(resourceSubsetResource);
                    } else {
                        resultElement = xmlDocument.createElement("resourceRef");
                        resultElement.setAttribute("resourceId", resourceSubsetResourceId);
                    }
                    Element resourceSubsetElement = createResourceSubsetElement(resourceSubset);
                    resultElement.appendChild(resourceSubsetElement);
                    break;
                case BLOCK:
                    Block block = value.getAsBlock();
                    String blockId = blockStore.register(block, Store.REGISTER_MODE.INSERT_STRICT);
                    if (blockId==null) {
                        resultElement = createBlockElement(block);
                    } else {
                        resultElement = xmlDocument.createElement("blockRef");
                        resultElement.setAttribute("blockId", blockId);
                    }
                    break;
                case NULL:
                default:
                    resultElement = xmlDocument.createElement("null");
            }
            return resultElement;
        }
        
    }
    
    
    private class XmlProcessor {
        
        final Element documentElement;
        
        Document document;
        
        Map<Resource, Map<String, Resource.Splitting.Part>> resourceSplittingPartMap;
        
        
        public XmlProcessor(org.w3c.dom.Document xmlDocument) throws ParseException {
            Element rootElement = xmlDocument.getDocumentElement();
            if (!rootElement.getTagName().equals("document")) {
                throw new ParseException("Document element not found");
            }
            documentElement = rootElement;
        }
        
        
        public Document process() {
            document = new Document();
            
            resourceSplittingPartMap = new HashMap<Resource, Map<String, Resource.Splitting.Part>>();
            
            Element headerElement = findFirstByTagName(documentElement, "header");
            if (headerElement!=null) {
                for (Element infoElement: findAllByTagName(headerElement, "info")) {
                    String infoName = infoElement.getAttribute("name");
                    String infoValue = infoElement.getAttribute("value");
                    if (infoName.equals("label")) {
                        document.setLabel(infoValue);
                    } else {
                        // FIXME/TODO
                    }
                }
            }
            
            Element periodsElement = findFirstByTagName(documentElement, "periods");
            if (periodsElement!=null) {
                for (Element periodElement: findAllByTagName(periodsElement, "period")) {
                    processPeriodElement(periodElement, true);
                }
                
            }

            // XXX for compatibility
            Element cyclesElement = findFirstByTagName(documentElement, "cycles");
            if (cyclesElement!=null) {
                for (Element cycleElement: findAllByTagName(cyclesElement, "cycle")) {
                    processPeriodElement(cycleElement, true);
                }
                
            }
            
            Element timingSetsElement = findFirstByTagName(documentElement, "timingSets");
            if (timingSetsElement!=null) {
                for (Element timingSetElement: findAllByTagName(timingSetsElement, "timingSet")) {
                    processTimingSetElement(timingSetElement, true);
                }
                
            }

            Element tagsElement = findFirstByTagName(documentElement, "tags");
            if (tagsElement!=null) {
                for (Element tagElement: findAllByTagName(tagsElement, "tag")) {
                    processTagElement(tagElement, true);
                }
                
            }

            Element resourcesElement = findFirstByTagName(documentElement, "resources");
            if (resourcesElement!=null) {
                for (Element resourceElement: findAllByTagName(resourcesElement, "resource")) {
                    processResourceElement(resourceElement, true);
                }
                
            }

            Element blocksElement = findFirstByTagName(documentElement, "blocks");
            if (blocksElement!=null) {
                for (Element blockElement: findAllByTagName(blocksElement, "block")) {
                    processBlockElement(blockElement, true);
                }
                
            }

            Element boardsElement = findFirstByTagName(documentElement, "boards");
            if (boardsElement!=null) {
                for (Element boardElement: findAllByTagName(boardsElement, "board")) {
                    processBoardElement(boardElement, true);
                }
                
            }

            // XXX for compatibility
            Element timeTablesElement = findFirstByTagName(documentElement, "timeTables");
            if (timeTablesElement!=null) {
                for (Element timeTableElement: findAllByTagName(timeTablesElement, "timeTable")) {
                    processBoardElement(timeTableElement, true);
                }
                
            }

            Element extraDataElement = findFirstByTagName(documentElement, "extraData");
            if (extraDataElement!=null) {
                Element extraDataValueElement = findFirstByTagName(extraDataElement, "*");
                if (extraDataValueElement!=null) {
                    Value extraData = processValueElement(extraDataValueElement);
                    document.getExtraData().getAccess().set(extraData);
                }
            }
            
            return document;
        }

        public Period processPeriodElement(Element periodElement, boolean addToStore) {
            Period period = new Period();
            
            period.setLabel(periodElement.getAttribute("label"));
            
            String termString = periodElement.getAttribute("term");
            if (!termString.isEmpty()) {
                try {
                    int term = Integer.parseInt(termString);
                    period.setTerm(term);
                } catch (NumberFormatException e) {
                }
            }
            
            String positionString = periodElement.getAttribute("position");
            if (!positionString.isEmpty()) {
                try {
                    int position = Integer.parseInt(positionString);
                    period.setPosition(position);
                } catch (NumberFormatException e) {
                }
            }
            
            if (addToStore) {
                Document.PeriodStore periodStore = document.getPeriodStore();
                String periodId = periodElement.getAttribute("id");
                if (periodId.isEmpty()) {
                    periodStore.register(period, Store.REGISTER_MODE.INSERT_AUTO);
                } else {
                    periodStore.register(period, periodId, Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
                }
            }
            
            return period;
        }
        
        public TimingSet processTimingSetElement(Element timingSetElement, boolean addToStore) {
            TimingSet timingSet = new TimingSet();
            timingSet.setLabel(timingSetElement.getAttribute("label"));
            for (Element timingSetEntryElement: findAllByTagName(timingSetElement, "entry")) {
                String timeString = timingSetEntryElement.getAttribute("time");
                String label = timingSetEntryElement.getAttribute("label");
                timingSet.add(new Time(timeString), label);
            }
            if (addToStore) {
                Document.TimingSetStore timingSetStore = document.getTimingSetStore();
                String timingSetId = timingSetElement.getAttribute("id");
                if (timingSetId.isEmpty()) {
                    timingSetStore.register(timingSet, Store.REGISTER_MODE.INSERT_AUTO);
                } else {
                    timingSetStore.register(timingSet, timingSetId, Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
                }
            }
            return timingSet;
        }
        
        public Tag processTagElement(Element tagElement, boolean addToStore) {
            Tag tag = new Tag();
            processAspectElementTo(tagElement, tag);
            String typeString = tagElement.getAttribute("type").toUpperCase();
            try {
                tag.setType(Tag.Type.valueOf(typeString));
            } catch (Exception e) {
            }
            if (addToStore) {
                Document.TagStore tagStore = document.getTagStore();
                String tagId = tagElement.getAttribute("id");
                if (tagId.isEmpty()) {
                    tagStore.register(tag, Store.REGISTER_MODE.INSERT_AUTO);
                } else {
                    tagStore.register(tag, tagId, Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
                }
            }
            return tag;
        }

        public Resource processResourceElement(Element resourceElement, boolean addToStore) {
            Resource resource = new Resource();
            processAspectElementTo(resourceElement, resource);
            String typeString = resourceElement.getAttribute("type").toUpperCase();
            try {
                resource.setType(Resource.Type.valueOf(typeString));
            } catch (Exception e) {
            }
            try {
                resource.setQuantity(Integer.parseInt(resourceElement.getAttribute("quantity")));
            } catch (Exception e) {
            }
            resource.setEmail(resourceElement.getAttribute("email"));
            
            Element splittingsElement = findFirstByTagName(resourceElement, "splittings");
            if (splittingsElement!=null) {
                Resource.SplittingManager splittingManager = resource.getSplittingManager();
                Map<String, Resource.Splitting.Part> splittingPartMap = new HashMap<String, Resource.Splitting.Part>();
                for (Element splittingElement: findAllByTagName(splittingsElement, "splitting")) {
                    String splittingLabel = splittingElement.getAttribute("label");
                    String splittingName = splittingElement.getAttribute("name");
                    Resource.Splitting splitting = resource.new Splitting(splittingLabel);
                    List<Element> splittingPartElements = findAllByTagName(splittingElement, "part");
                    for (Element splittingPartElement: splittingPartElements) {
                        String splittingPartLabel = splittingPartElement.getAttribute("label");
                        String splittingPartName = splittingPartElement.getAttribute("name");
                        Resource.Splitting.Part splittingPart = splitting.addPart(splittingPartLabel);
                        splittingPartMap.put(splittingName+"."+splittingPartName, splittingPart);
                    }
                    splittingManager.add(splitting);
                    resourceSplittingPartMap.put(resource, splittingPartMap);
                }
            }

            if (addToStore) {
                Document.ResourceStore resourceStore = document.getResourceStore();
                String resourceId = resourceElement.getAttribute("id");
                if (resourceId.isEmpty()) {
                    resourceStore.register(resource, Store.REGISTER_MODE.INSERT_AUTO);
                } else {
                    resourceStore.register(resource, resourceId, Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
                }
            }
            
            return resource;
        }

        public void processAspectElementTo(Element aspectElement, Aspect aspect) {
            Document.TimingSetStore timingSetStore = document.getTimingSetStore();
            Document.PeriodStore periodStore = document.getPeriodStore();
            
            aspect.setLabel(aspectElement.getAttribute("label"));
            aspect.setAcronym(aspectElement.getAttribute("acronym"));
            aspect.setColor(new Color(aspectElement.getAttribute("color")));
            aspect.setTimingSetEnabled(aspectElement.getAttribute("timingSetEnabled").equals("1")); // TODO: or "true"
            Element timingSetsElement = findFirstByTagName(aspectElement, "timingSets");
            if (timingSetsElement!=null) {
                Aspect.TimingSetManager timingSetManager = aspect.getTimingSetManager();
                for (Element timingSetElement: findAllByTagName(timingSetsElement, "timingSet")) {
                    TimingSet timingSet = processTimingSetElement(timingSetElement, true);
                    String periodId = timingSetElement.getAttribute("periodId");
                    
                    // XXX for compatibility
                    if (periodId.isEmpty()) {
                        periodId = timingSetElement.getAttribute("cycleId");
                    }
                    
                    if (periodId.isEmpty()) {
                        timingSetManager.setDefaultTimingSet(timingSet);
                    } else {
                        Period period = periodStore.get(periodId);
                        if (period!=null) {
                            timingSetManager.setPeriodTimingSet(period, timingSet);
                        }
                    }
                }
                for (Element timingSetRefElement: findAllByTagName(timingSetsElement, "timingSetRef")) {
                    TimingSet timingSet = timingSetStore.get(timingSetRefElement.getAttribute("timingSetId"));
                    if (timingSet!=null) {
                        String periodId = timingSetRefElement.getAttribute("periodId");

                        // XXX for compatibility
                        if (periodId.isEmpty()) {
                            periodId = timingSetRefElement.getAttribute("cycleId");
                        }
                        
                        if (periodId.isEmpty()) {
                            timingSetManager.setDefaultTimingSet(timingSet);
                        } else {
                            Period period = periodStore.get(periodId);
                            if (period!=null) {
                                timingSetManager.setPeriodTimingSet(period, timingSet);
                            }
                        }
                    }
                }
            }
            
            aspect.setTimeLimitEnabled(aspectElement.getAttribute("timeLimitEnabled").equals("1")); // TODO: or "true"
            Element timeLimitsElement = findFirstByTagName(aspectElement, "timeLimits");
            if (timeLimitsElement!=null) {
                Aspect.TimeLimitManager timeLimitManager = aspect.getTimeLimitManager();
                for (Element timeLimitElement: findAllByTagName(timeLimitsElement, "timeLimit")) {
                    TimeLimit timeLimit = processTimeLimitElement(timeLimitElement);
                    if (timeLimit!=null) {
                        String periodId = timeLimitElement.getAttribute("periodId");

                        // XXX for compatibility
                        if (periodId.isEmpty()) {
                            periodId = timeLimitElement.getAttribute("cycleId");
                        }
                        
                        if (periodId.isEmpty()) {
                            timeLimitManager.setDefaultTimeLimit(timeLimit);
                        } else {
                            Period period = periodStore.get(periodId);
                            if (period!=null) {
                                timeLimitManager.setPeriodTimeLimit(period, timeLimit);
                            }
                        }
                    }
                }
            }
        }
        
        // TODO
        public TimeLimit processTimeLimitElement(Element timeLimitElement) {
            TimeLimit timeLimit = null;
            String typeString = timeLimitElement.getAttribute("type");
            if (typeString.equals("custom")) {
                boolean startState = (timeLimitElement.getAttribute("startState").equals("1"));
                List<Time> times = new ArrayList<Time>();
                for (Element timeLimitSwitchElement: findAllByTagName(timeLimitElement, "switch")) {
                    times.add(new Time(timeLimitSwitchElement.getAttribute("time")));
                }
                timeLimit = new CustomTimeLimit(startState, times);
            } else {
                // FIXME/TODO
            }
            return timeLimit;
        }
        
        public Block processBlockElement(Element blockElement, boolean addToStore) {
            Block block = new Block();
            block.setLabel(blockElement.getAttribute("label"));
            block.setLength(Time.parseSeconds(blockElement.getAttribute("length")));
            
            for (Element activityElement: findAllByTagName(blockElement, "activity")) {
                processActivityElementToBlock(activityElement, block);
            }

            // XXX for compatibility
            for (Element eventElement: findAllByTagName(blockElement, "event")) {
                processActivityElementToBlock(eventElement, block);
            }
            
            if (addToStore) {
                Document.BlockStore blockStore = document.getBlockStore();
                String blockId = blockElement.getAttribute("id");
                if (blockId.isEmpty()) {
                    blockStore.register(block, Store.REGISTER_MODE.INSERT_AUTO);
                } else {
                    blockStore.register(block, blockId, Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
                }
            }
            return block;
        }
        
        public void processActivityElementToBlock(Element activityElement, Block block) {
            Document.PeriodStore periodStore = document.getPeriodStore();
            Document.TagStore tagStore = document.getTagStore();
            Document.ResourceStore resourceStore = document.getResourceStore();
            
            List<Period> periods = new ArrayList<Period>();
            for (Element periodElement: findAllByTagName(activityElement, "periodRef")) {
                Period period = periodStore.get(periodElement.getAttribute("periodId"));
                if (period!=null) {
                    periods.add(period);
                }
            }
            // XXX for compatibility
            for (Element cycleElement: findAllByTagName(activityElement, "cycleRef")) {
                Period period = periodStore.get(cycleElement.getAttribute("cycleId"));
                if (period!=null) {
                    periods.add(period);
                }
            }
            if (periods.isEmpty()) {
                return;
            }
            Activity activity = new Activity();
            Activity.TagManager tagManager = activity.getTagManager();
            activity.setLabel(activityElement.getAttribute("label"));
            for (Element tagElement: findAllByTagName(activityElement, "tagRef")) {
                Tag tag = tagStore.get(tagElement.getAttribute("tagId"));
                if (tag!=null) {
                    tagManager.add(tag);
                }
            }
            
            Activity.ResourceManager resourceManager = activity.getResourceManager();
            for (Element resourceElement: findAllByTagName(activityElement, "resourceRef")) {
                Resource resource = resourceStore.get(resourceElement.getAttribute("resourceId"));
                if (resource!=null) {
                    Element subsetElement = findFirstByTagName(resourceElement, "subset");
                    if (subsetElement==null) {
                        resourceManager.add(resource);
                    } else {
                        ResourceSubset resourceSubset = processResourceSubsetElement(subsetElement, resource);
                        if (resourceSubset!=null) {
                            resourceManager.add(resourceSubset);
                        }
                    }
                }
            }
            
            Block.ActivityManager activityManager = block.getActivityManager();
            activityManager.add(activity, periods);
        }
        
        public ResourceSubset processResourceSubsetElement(Element resourceSubsetElement, Resource resource) {
            ResourceSubset resourceSubset = null;
            Map<String, Resource.Splitting.Part> splittingPartMap = resourceSplittingPartMap.get(resource);
            String type = resourceSubsetElement.getAttribute("type");
            List<ResourceSubset> children = new ArrayList<ResourceSubset>();
            for (Element childSubsetElement: findAllByTagName(resourceSubsetElement, "subset")) {
                ResourceSubset child = processResourceSubsetElement(childSubsetElement, resource);
                if (child!=null) {
                    children.add(child);
                }
            }
            ResourceSubset firstChild = null;
            if (!children.isEmpty()) {
                firstChild = children.get(0);
            }
            if (type.equals("part")) {
                String partName = resourceSubsetElement.getAttribute("partName");
                Resource.Splitting.Part splittingPart = null;
                if (splittingPartMap!=null && !partName.isEmpty()) {
                    splittingPart = splittingPartMap.get(partName);
                }
                if (splittingPart!=null) {
                    resourceSubset = new ResourceSubset.SplittingPart(splittingPart);
                }
            } else if (type.equals("union")) {
                resourceSubset = new ResourceSubset.Union(children);
            } else if (type.equals("intersection")) {
                resourceSubset = new ResourceSubset.Intersection(children);
            } else if (type.equals("difference")) {
                resourceSubset = new ResourceSubset.Difference(children);
            } else if (type.equals("symmetricDifference")) {
                resourceSubset = new ResourceSubset.SymmetricDifference(children);
            } else if (type.equals("inverse")) {
                if (firstChild!=null) {
                    resourceSubset = new ResourceSubset.Inverse(firstChild);
                }
            } else if (type.equals("whole")) {
                resourceSubset = new ResourceSubset.Whole(resource);
            }
            return resourceSubset;
        }

        public Board processBoardElement(Element boardElement, boolean addToStore) {
            Document.BlockStore blockStore = document.getBlockStore();
            
            Board board = new Board();
            board.setLabel(boardElement.getAttribute("label"));
            for (Element entryElement: findAllByTagName(boardElement, "entry")) {
                Block block = blockStore.get(entryElement.getAttribute("blockId"));
                Time time = new Time(entryElement.getAttribute("time"));
                if (block!=null) {
                    board.add(block, time);
                }
            }
            
            if (addToStore) {
                Document.BoardStore boardStore = document.getBoardStore();
                String boardId = boardElement.getAttribute("id");
                if (boardId.isEmpty()) {
                    boardStore.register(board, Store.REGISTER_MODE.INSERT_AUTO);
                } else {
                    boardStore.register(board, boardId, Store.REGISTER_MODE.INSERT_PUSH_ASIDE);
                }
            }
            
            return board;
        }
        
        public Value processValueElement(Element valueElement) {
            if (valueElement==null) {
                return new Value(Value.Type.NULL);
            }

            String tagName = valueElement.getTagName();
            String valueStr = "";
            if (valueElement.hasAttribute("value")) {
                valueStr = valueElement.getAttribute("value");
            }
            
            if (tagName.equals("null")) {
                return new Value(Value.Type.NULL);
            } else if (tagName.equals("boolean")) {
                return new Value(!valueStr.matches("|0|false"));
            } else if (tagName.equals("byte")) {
                return new Value(getNumberValue(valueStr).byteValue());
            } else if (tagName.equals("short")) {
                return new Value(getNumberValue(valueStr).shortValue());
            } else if (tagName.equals("int")) {
                return new Value(getNumberValue(valueStr).intValue());
            } else if (tagName.equals("long")) {
                return new Value(getNumberValue(valueStr).longValue());
            } else if (tagName.equals("float")) {
                return new Value(getNumberValue(valueStr).floatValue());
            } else if (tagName.equals("double")) {
                return new Value(getNumberValue(valueStr).doubleValue());
            } else if (tagName.equals("char")) {
                if (valueStr.isEmpty()) {
                    return new Value(Value.Type.CHAR);
                } else {
                    return new Value(valueStr.charAt(0));
                }
            } else if (tagName.equals("string")) {
                return new Value(valueStr);
            } else if (tagName.equals("map")) {
                Value mapValue = new Value(Value.Type.MAP);
                Value.ValueMap map = mapValue.getAccess().get().getAsMap();
                for (Element entryElement: findAllByTagName(valueElement, "entry")) {
                    Value keyValue = new Value(Value.Type.NULL);
                    Value subValue = new Value(Value.Type.NULL);
                    Element keyWrapperElement = findFirstByTagName(entryElement, "key");
                    if (keyWrapperElement!=null) {
                        Element keyElement = findFirstByTagName(keyWrapperElement, "*");
                        if (keyElement!=null) {
                            keyValue = processValueElement(keyElement);
                        }
                    }
                    Element subValueWrapperElement = findFirstByTagName(entryElement, "value");
                    if (subValueWrapperElement!=null) {
                        Element subValueElement = findFirstByTagName(subValueWrapperElement, "*");
                        if (subValueElement!=null) {
                            subValue = processValueElement(subValueElement);
                        }
                    }
                    map.put(keyValue, subValue);
                }
                return mapValue;
            } else if (tagName.equals("list")) {
                Value listValue = new Value(Value.Type.LIST);
                Value.ValueList list = listValue.getAccess().get().getAsList();
                for (Element itemElement: findAllByTagName(valueElement, "*")) {
                    list.add(processValueElement(itemElement));
                }
                return listValue;
            } else if (tagName.equals("set")) {
                Value setValue = new Value(Value.Type.SET);
                Value.ValueSet set = setValue.getAccess().get().getAsSet();
                for (Element itemElement: findAllByTagName(valueElement, "*")) {
                    set.add(processValueElement(itemElement));
                }
                return setValue;
                
                // XXX for compatibility
            } else if (tagName.equals("period") || tagName.equals("cycle")) {
                Period period = processPeriodElement(valueElement, false);
                return new Value(period);
                
                // XXX for compatibility
            } else if (tagName.equals("periodRef") || tagName.equals("cycleRef")) {
                Period period;
                String periodId = valueElement.getAttribute("periodId");
                
                // XXX for compatibility
                if (periodId.isEmpty()) {
                    periodId = valueElement.getAttribute("cycleId");
                }
                
                Document.PeriodStore periodStore = document.getPeriodStore();
                if (periodStore.containsId(periodId)) {
                    period = periodStore.get(periodId);
                } else {
                    period = new Period();
                }
                return new Value(period);
            } else if (tagName.equals("timingSet")) {
                TimingSet timingSet = processTimingSetElement(valueElement, false);
                return new Value(timingSet);
            } else if (tagName.equals("timingSetRef")) {
                TimingSet timingSet;
                String timingSetId = valueElement.getAttribute("timingSetId");
                Document.TimingSetStore timingSetStore = document.getTimingSetStore();
                if (timingSetStore.containsId(timingSetId)) {
                    timingSet = timingSetStore.get(timingSetId);
                } else {
                    timingSet = new TimingSet();
                }
                return new Value(timingSet);
            } else if (tagName.equals("timeLimit")) {
                TimeLimit timeLimit = processTimeLimitElement(valueElement);
                return new Value(timeLimit);
            } else if (tagName.equals("tag")) {
                Tag tag = processTagElement(valueElement, false);
                return new Value(tag);
            } else if (tagName.equals("tagRef")) {
                Tag tag;
                String tagId = valueElement.getAttribute("tagId");
                Document.TagStore tagStore = document.getTagStore();
                if (tagStore.containsId(tagId)) {
                    tag = tagStore.get(tagId);
                } else {
                    tag = new Tag();
                }
                return new Value(tag);
            } else if (tagName.equals("resource")) {
                Resource resource = processResourceElement(valueElement, false);
                return new Value(resource);
            } else if (tagName.equals("resourceRef")) {
                Resource resource;
                String resourceId = valueElement.getAttribute("resourceId");
                Document.ResourceStore resourceStore = document.getResourceStore();
                if (resourceStore.containsId(resourceId)) {
                    resource = resourceStore.get(resourceId);
                } else {
                    resource = new Resource();
                }
                
                // FIXME / TODO: resourceSubset
                
                return new Value(resource);
            } else if (tagName.equals("block")) {
                Block block = processBlockElement(valueElement, false);
                return new Value(block);
            } else if (tagName.equals("blockRef")) {
                Block block;
                String blockId = valueElement.getAttribute("blockId");
                Document.BlockStore blockStore = document.getBlockStore();
                if (blockStore.containsId(blockId)) {
                    block = blockStore.get(blockId);
                } else {
                    block = new Block();
                }
                return new Value(block);
            } else {
                return new Value(Value.Type.NULL);
            }
        }
        
        protected Number getNumberValue(String valueStr) {
            Number result = Integer.valueOf(0);
            if (!valueStr.isEmpty()) {
                if (valueStr.indexOf('.')==(-1)) {
                    try {
                        result = Long.parseLong(valueStr);
                    } catch (NumberFormatException e) {
                    }
                } else {
                    try {
                        result = Double.parseDouble(valueStr);
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return result;
        }
        
        public Element findFirstByTagName(Element element, String tagName) {
            boolean matchAll = (tagName.equals("*"));
            NodeList nodeList = element.getChildNodes();
            int size = nodeList.getLength();
            for (int i=0; i<size; i++) {
                Node childNode = nodeList.item(i);
                if (childNode.getNodeType()==Node.ELEMENT_NODE) {
                    Element childElement = (Element)childNode;
                    if (matchAll || childElement.getTagName().equals(tagName)) {
                        return childElement;
                    }
                }
            }
            return null;
        }
        
        public List<Element> findAllByTagName(Element element, String tagName) {
            List<Element> elements = new ArrayList<Element>();
            boolean matchAll = (tagName.equals("*"));
            NodeList nodeList = element.getChildNodes();
            int size = nodeList.getLength();
            for (int i=0; i<size; i++) {
                Node childNode = nodeList.item(i);
                if (childNode.getNodeType()==Node.ELEMENT_NODE) {
                    Element childElement = (Element)childNode;
                    if (matchAll || childElement.getTagName().equals(tagName)) {
                        elements.add(childElement);
                    }
                }
            }
            return elements;
        }
        
    }
    
}
