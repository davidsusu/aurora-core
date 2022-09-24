package hu.webarticum.aurora.core.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.Collator;
import java.text.Normalizer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Labeled extends Serializable {

    public String getLabel();
    
    public static class LabeledComparator implements Comparator<Labeled>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Labeled labeled1, Labeled labeled2) {
            return StringComparator.compareStatic(labeled1.getLabel(), labeled2.getLabel());
        }

        public static int compareStatic(Labeled labeled1, Labeled labeled2) {
            return StringComparator.compareStatic(labeled1.getLabel(), labeled2.getLabel());
        }
        
    }

    public static class GenericLabeledComparator<L extends Labeled> implements Comparator<L>, Serializable {

        private static final long serialVersionUID = 1L;
        
        @Override
        public int compare(L labeled1, L labeled2) {
            return StringComparator.compareStatic(labeled1.getLabel(), labeled2.getLabel());
        }
        
    }

    public static class StringComparator implements Comparator<String>, Serializable {

        private static final long serialVersionUID = 1L;
        
        
        private static final Pattern DIGITS_PATTERN = Pattern.compile("\\d+");

        
        private enum CharacterType { LETTER, DIGIT, OTHER }
        
        
        @Override
        public int compare(String string1, String string2) {
            return compareStatic(string1, string2);
        }
        
        public static int compareStatic(String string1, String string2) {
            if (string1.equals(string2)) {
                return 0;
            }
            
            int effectiveCmp = compareTokenLists(getEffectiveTokens(string1), getEffectiveTokens(string2));
            if (effectiveCmp != 0) {
                return effectiveCmp;
            }
            
            int intermediateCmp = compareTokenLists(getIntermediateTokens(string1), getIntermediateTokens(string2));
            if (intermediateCmp != 0) {
                return intermediateCmp;
            }
            
            return string1.compareTo(string2);
        }
        
        private static List<String> getEffectiveTokens(String string) {
            String normalizedString = Normalizer.normalize(string, Normalizer.Form.NFC);
            int length = normalizedString.length();
            List<String> result = new ArrayList<String>();
            CharacterType currentType = CharacterType.OTHER;
            StringBuilder currentBuilder = new StringBuilder();
            for (int c = 0; c < length; c++) {
                char ch = normalizedString.charAt(c);
                CharacterType newType = getCharacterType(ch);
                if (newType == CharacterType.OTHER) {
                    if (currentBuilder.length() > 0) {
                        result.add(currentBuilder.toString());
                        currentBuilder = new StringBuilder();
                    }
                    currentType = CharacterType.OTHER;
                } else {
                    if (currentType != newType) {
                        if (currentBuilder.length() > 0) {
                            result.add(currentBuilder.toString());
                        }
                        currentBuilder = new StringBuilder();
                        currentType = newType;
                    }
                    currentBuilder.append(ch);
                }
            }
            if (currentBuilder.length() > 0) {
                result.add(currentBuilder.toString());
            }
            return result;
        }
        
        private static CharacterType getCharacterType(char ch) {
            if (Character.isLetter(ch)) {
                return CharacterType.LETTER;
            } else if (Character.isDigit(ch)) {
                return CharacterType.DIGIT;
            } else {
                return CharacterType.OTHER;
            }
        }
        
        private static List<String> getIntermediateTokens(String string) {
            List<String> result = new ArrayList<String>();
            Pattern pattern = Pattern.compile("\\P{LD}");
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                result.add(matcher.group());
            }
            return result;
        }
        
        private static int compareTokenLists(List<String> tokens1, List<String> tokens2) {
            int result = 0;
            int size1 = tokens1.size();
            int size2 = tokens2.size();
            int minSize = Math.min(size1, size2);
            for (int i = 0; (result == 0 && i < minSize); i++) {
                result = compareTokens(tokens1.get(i), tokens2.get(i));
            }
            if (result == 0) {
                return Integer.compare(size1, size2);
            }
            return result;
        }
        
        private static int compareTokens(String token1, String token2) {
            boolean isNumeric1 = DIGITS_PATTERN.matcher(token1).matches();
            boolean isNumeric2 = DIGITS_PATTERN.matcher(token2).matches();
            
            if (isNumeric1) {
                if (isNumeric2) {
                    return compareNumericTokens(token1, token2);
                } else {
                    return -1;
                }
            } else if (isNumeric2) {
                return 1;
            } else {
                Locale locale = new Locale("HU_hu");
                Collator collator = Collator.getInstance(locale);
                collator.setStrength(Collator.PRIMARY);
                return collator.compare(token1, token2);
            }
        }
        
        private static int compareNumericTokens(String token1, String token2) {
            if (token1.length() <= 18 && token2.length() <= 18) {
                long long1 = Long.parseLong(token1);
                long long2 = Long.parseLong(token2);
                return Long.compare(long1, long2);
            } else {
                BigInteger bigInteger1 = new BigInteger(token1);
                BigInteger bigInteger2 = new BigInteger(token2);
                return bigInteger1.compareTo(bigInteger2);
            }
        }
        
    }
    

    public static class StringLabeled implements Labeled {

        private static final long serialVersionUID = 1L;

        
        private String str;
        
        
        public StringLabeled(String str) {
            this.str = str;
        }
        

        @Override
        public String getLabel() {
            return str;
        }
        
        @Override
        public String toString() {
            return str;
        }
        
    }
    
    
    public static class Wrapper extends GeneralWrapper<Labeled> implements Labeled {

        private static final long serialVersionUID = 1L;
        
        
        public Wrapper(Labeled labeled) {
            super(labeled);
        }
        
        
        @Override
        public String getLabel() {
            return get().getLabel();
        }
        
        @Override
        public String toString() {
            return get().getLabel();
        }

    }

    public static class GenericWrapper<T extends Labeled> extends GeneralWrapper<T> implements Labeled {

        private static final long serialVersionUID = 1L;
        
        
        public GenericWrapper(T labeled) {
            super(labeled);
        }
        
        
        @Override
        public String getLabel() {
            return get().getLabel();
        }
        
        @Override
        public String toString() {
            return get().getLabel();
        }

    }
    
    public static class PairWrapper<T> extends GeneralWrapper<T> implements Labeled {

        private static final long serialVersionUID = 1L;

        
        private Labeled labeled;
        
        
        public PairWrapper(String str, T item) {
            super(item);
            this.labeled = new StringLabeled(str);
        }

        public PairWrapper(Labeled labeled, T item) {
            super(item);
            this.labeled = labeled;
        }
        
        
        @Override
        public String getLabel() {
            return labeled.getLabel();
        }
        
        @Override
        public String toString() {
            return labeled.getLabel();
        }
        
    }

    public static class ListWrapper extends AbstractList<Labeled> { // NOSONAR

        private List<? extends Labeled> wrappedList;
        
        
        public ListWrapper(List<? extends Labeled> wrappedList) {
            this.wrappedList = wrappedList;
        }
        
        
        @Override
        public Labeled get(int index) {
            return new Wrapper(wrappedList.get(index));
        }

        @Override
        public int size() {
            return wrappedList.size();
        }
        
    }
    
    public static class LabeledStore<L extends Labeled> extends Store<L> {
        
        private static final long serialVersionUID = 1L;

        
        public LabeledStore() {
            this(new GenericLabeledComparator<L>());
        }

        public LabeledStore(Comparator<L> comparator) {
            super(comparator);
        }
        

        @Override
        public String getDefaultIdFor(L item) {
            return simplifyString(item.getLabel());
        }
        
        public L getFirstByLabel(String label) {
            for (L item: this) {
                if (item.getLabel().equals(label)) {
                    return item;
                }
            }
            return null;
        }

        public List<L> getAllByLabel(String label) {
            List<L> result = new ArrayList<L>();
            for (L item: this) {
                if (item.getLabel().equals(label)) {
                    result.add(item);
                }
            }
            return result;
        }

        private String simplifyString(String label) {
            return Normalizer.normalize(label, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("\\W+", " ")
                .toLowerCase()
                .trim()
                .replace(' ', '_')
            ;
        }

    }
    
}
