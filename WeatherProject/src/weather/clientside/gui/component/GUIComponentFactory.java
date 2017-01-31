package weather.clientside.gui.component;

import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import weather.GeneralService;
import weather.clientside.utilities.MyDefaultTableModel;
import weather.common.data.HourlyTimeType;
import weather.common.data.User;
import weather.common.data.UserType;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.bookmark.BookmarkRank;
import weather.common.data.bookmark.BookmarkType;
import weather.common.data.bookmark.CategoryViewRights;
import weather.common.data.diary.BarometricPressureTrendType;
import weather.common.data.diary.CloudType;
import weather.common.data.diary.DewPointTrendType;
import weather.common.data.diary.PrecipitationChanceType;
import weather.common.data.diary.PressureChangeRelationType;
import weather.common.data.diary.RelativeHumidityTrendType;
import weather.common.data.diary.TemperaturePredictionType;
import weather.common.data.diary.TemperatureTrendType;
import weather.common.data.diary.WindDirectionSummaryType;
import weather.common.data.diary.WindDirectionType;
import weather.common.data.diary.WindSpeedType;
import weather.common.data.diary.YesNoType;
import weather.common.data.resource.Resource;
import weather.common.dbms.DBMSBookmarkCategoriesManager;
import weather.common.dbms.DBMSBookmarkEventTypesManager;
import weather.common.dbms.DBMSEventManager;
import weather.common.gui.component.IconProperties;


/**
 * Factory class for creating or initializing GUI components.
 *
 * NOTE: Initializing is necessary when using NetBeans form builders because
 * the generated code conflicts with creating new objects.
 *
 * @author Eric Subach (2010)
 * @author Xiang Li (2014)
 */
public class GUIComponentFactory {
    /**
     * Make a <code>ComboBox</code> that lists all bookmark categories.
     *
     * @return A <code>ComboBox</code> that lists all bookmark categories.
     */
    public static JComboBox getBookmarkCategoryBox (DBMSEventManager eventMgr) {
        JComboBox<String> comboBox = new JComboBox<String>();

        for (BookmarkCategory bc : eventMgr.getAllBookmarkCategories ()) {
            comboBox.addItem(bc.getName ());
        }

        return (comboBox);
    }


    /**
     * Make a <code>ComboBox</code> that lists all bookmark rankings.
     *
     * @return A <code>ComboBox</code> that lists all bookmark rankings.
     */
    public static JComboBox getRankingBox () {
        JComboBox<String> comboBox = new JComboBox<String>();

        for (BookmarkRank e : BookmarkRank.values ()) {
            comboBox.addItem (e.toString ());
        }

        return (comboBox);
    }

    /**
     * Gets all the of enumerations for bookmark classifications.
     * @return A combo box with the values for bookmark classification enumerations.
     */
    public static JComboBox getBookmarkClassificationBox(){
        JComboBox<String> comboBox = new JComboBox<String>();

        for (BookmarkDuration e : BookmarkDuration.values ()) {
            comboBox.addItem (e.toString ());
        }

        return (comboBox);
    }

    //=========================================================================
    //=========================================================================


    /**
     * Initialize a combo box that lists all bookmark categories.
     * @param comboBox The <code>ComboBox</code> to initialize.
     * @param eventMgr The bookmark event manager.
     * @param user The current user.
     */
    public static void initBookmarkCategoryBox (JComboBox<String> comboBox, DBMSEventManager eventMgr, User user) {
        for (BookmarkCategory bc : eventMgr.getAllBookmarkCategories ()) {
            if(user.getUserType()==UserType.administrator || 
                    bc.getViewRights() == CategoryViewRights.system_wide || 
                    user.getUserNumber() == bc.getCreatedBy()) {
                comboBox.addItem(bc.getName ());
            }
        }
    }
    
    /**
     * Initialize a combo box that lists all bookmark categories except the default.
     *
     * @param comboBox The <code>ComboBox</code> to initialize.
     * @param eventMgr The bookmark event manager.
     * @param user The current user.
     */
    public static void initBookmarkCategoryBoxWithoutDefault(JComboBox<String> comboBox, DBMSEventManager eventMgr, User user) {
        for (BookmarkCategory bc : eventMgr.getAllBookmarkCategories()) {
            if(bc.getName().equals("<Uncategorized>")) {
                continue;
            }
            if (user.getUserType() == UserType.administrator
                    || bc.getViewRights() == CategoryViewRights.system_wide
                    || user.getUserNumber() == bc.getCreatedBy()) {
                comboBox.addItem(bc.getName());
            }
        }
    }

    /**
     * Initialize a <code>ComboBox</code> that lists all bookmark rankings.
     */
    public static void initRankingBox (JComboBox<String> comboBox) {
        for (BookmarkRank e : BookmarkRank.values ()) {
            comboBox.addItem (e.toString ());
        }
    }
    
    /**
     * Generates a <code>JTable</code> containing all of the Bookmark
     * Categories and their descriptions.
     * 
     * @param categoryManager The MYSQLBookmarkCategoriesManager for the database.
     * @param currentUser The current user.
     * @return A <code>JTable</code> containing all of the Bookmark Categories.
     */
    public static JTable getBookmarkCategoryTable(DBMSBookmarkCategoriesManager
            categoryManager, User currentUser){
        JTable table = new JTable();
        
        String[] colNames = {"Sub-Categories", "Descriptions"};
        // Get all bookmark categories.
        Vector<BookmarkCategory> categories = categoryManager.obtainAll();
        
        // Remove private categories if appropriate
        if(currentUser.getUserType()!=UserType.administrator){
            int size = categories.size(), i=0;
            while(i<size){
                BookmarkCategory bc = categories.elementAt(i);
                if(bc.getViewRights()==CategoryViewRights.instructor_only && 
                        bc.getCreatedBy()!=currentUser.getUserNumber()){
                    categories.remove(bc);
                    size--;
                }
                else i++;
            }
        }
        BookmarkCategory category;
        int colLength = categories.size ();


        // Set info about table height and width.
        table.setModel (new MyDefaultTableModel (colLength, colNames.length));

        // Add data.
        for (int i = 0; i < colLength; i++) {
            category = categories.get (i);
            table.setValueAt (category.getName(), i, 0);
            table.setValueAt (category.getNotes(), i, 1);
        }

        table.updateUI ();
        
        return table;
    }
    
    /**
     * Generates a <code>JTable</code> containing all of the Bookmark
     * Sub-categories and their descriptions.
     *
     * TODO: check for correctness and if this is a duplicate of above code. See the code below as well.
     * 
     * @param eventMgr MySQLBookmarkTypesManager.
     * @param currentUser The current user.
     * @return A <code>JTable</code> containing all of the Bookmark Sub-categories.
     */
    public static JTable getBookmarkSubCategoryTable(
            DBMSBookmarkEventTypesManager eventMgr, User currentUser){
        JTable table = new JTable ();

        String[] colNames = {"Sub-Categories", "Descriptions"};
        // Get all bookmark categories.
        Vector<BookmarkType> categories = eventMgr.obtainAll();
        
        // Remove private categories if appropriate
        if(currentUser.getUserType()!=UserType.administrator){
            int size = categories.size(), i=0;
            while(i<size){
                BookmarkType subCategory = categories.elementAt(i);
                if(subCategory.getViewRights()==
                        CategoryViewRights.instructor_only && 
                        subCategory.getCreatedBy()!=
                        currentUser.getUserNumber()){
                    categories.remove(subCategory);
                    size--;
                }
                else i++;
            }
        }
        
        BookmarkType category;
        int colLength = categories.size ();


        // Set info about table height and width.
        table.setModel (new MyDefaultTableModel (colLength, colNames.length));

        // Add data.
        for (int i = 0; i < colLength; i++) {
            category = categories.get (i);
            table.setValueAt (category.getName (), i, 0);
            table.setValueAt (category.getNotes (), i, 1);
        }

        table.updateUI ();

        return (table);
    }

    /**
     * Create a <code>JTable</code> that lists all bookmark categories and their descriptions.
     *
     * @param eventMgr MySQLBookmarkTypesManager.
     * @param catNum The category number.
     * @param currentUser The current user.
     * @return A <code>JTable</code> containing all sub-categories of the given category.
     */
    public static JTable getBookmarkSubCategoryTable (DBMSBookmarkEventTypesManager 
            eventMgr, int catNum, User currentUser) {
        JTable table = new JTable ();

        String[] colNames = {"Sub-Categories", "Descriptions"};
        // Get all bookmark categories.
        Vector<BookmarkType> categories = eventMgr.obtainAll(catNum);
        
        // Remove private categories if appropriate
        if(currentUser.getUserType()!=UserType.administrator){
            int size = categories.size(), i=0;
            while(i<size){
                BookmarkType subCategory = categories.elementAt(i);
                if(subCategory.getViewRights()==
                        CategoryViewRights.instructor_only && 
                        subCategory.getCreatedBy()!=
                        currentUser.getUserNumber()){
                    categories.remove(subCategory);
                    size--;
                }
                else i++;
            }
        }
        
        BookmarkType category;
        int colLength = categories.size ();


        // Set info about table height and width.
        table.setModel (new MyDefaultTableModel (colLength, colNames.length));

        // Add data.
        for (int i = 0; i < colLength; i++) {
            category = categories.get (i);
            table.setValueAt (category.getName (), i, 0);
            table.setValueAt (category.getNotes (), i, 1);
        }

        table.updateUI ();

        return (table);
    }


    /**
     * Populate as <code>ComboBox</code> with instances of <code>Resource</code>.
     * Also checks which resource from list user should see.
     * @param comboBox The <code>ComboBox</code> to be filled with instances of 
     * <code>Resource</code>.
     * @param resources The resources going in the <code>ComboBox</code>.  This
     * is a <code>Vector</code> of class <code>Resource</code>.
     * @param genService A <code>GeneralService</code> that can provide the
     * current <code>User</code>.
     * @param addNone True if "None" should be a listed option, false otherwise.
     */
    private static void initResourceComboBox (JComboBox<ResourceListCellItem> comboBox,
                                                Vector<Resource> resources,
                                                GeneralService genService,
                                                boolean addNone) {
        // Set rendering.
        ListCellRenderer<ResourceListCellItem> renderer = new ResourceListCellRenderer();
        comboBox.setRenderer(renderer);

        // Get icons.
        Icon visible = IconProperties.getResourceVisibleIconImage();
        Icon invisible = IconProperties.getResourceInvisibleIconImage();
        Icon inactive = IconProperties.getResourceInactiveIconImage();

        // Determine user's viewing rights (privileged users should see all resources).
        boolean canSeeAllResources;
        User user = genService.getUser();
        if (user == null) {
            canSeeAllResources = false;
        } else {
            canSeeAllResources = user.getUserType() == UserType.administrator 
                    || user.getUserType() == UserType.instructor;
        }
        
        // Add resources to combobox.
        for(Resource resource : resources){
            // Show only resources the user should see.
            if(canSeeAllResources || resource.isVisible()) {
                if (resource.isVisible()) {
                    comboBox.addItem(new ResourceListCellItem(resource.getResourceName(), visible, true));
                } else if (resource.isActive()) {
                    comboBox.addItem(new ResourceListCellItem(resource.getResourceName(), invisible, true));
                } else {
                    comboBox.addItem(new ResourceListCellItem(resource.getResourceName(), inactive, false));
                }
            }
        }
        
        // Stop here if not adding none.
        if(!addNone) {
            return;
        }
        
        // Add none.
        ResourceListCellItem none = new ResourceListCellItem();
        comboBox.addItem(none);

        // Select none by default.
        comboBox.setSelectedItem(none);
    }

    /**
     * Fills a given <code>JComboBox</code> with the camera resources (instances
     * of <code>Resource</code>) provided by a <code>GeneralService</code>.
     * @param comboBox The <code>ComboBox</code> to be filled with instances of
     * <code>Resource</code>.  These are camera resources.
     * @param genService A <code>GeneralService</code> that can provide the
     * camera instances of <code>Resource</code>.
     * @param addNone True if "None" should be a listed option, false otherwise.
     */
    public static void initCameraComboBox (JComboBox<ResourceListCellItem> comboBox,
                                           GeneralService genService,
                                           boolean addNone) {
        // Get resources.
        Vector<Resource> resources = genService.getWeatherCameraResources();
        initResourceComboBox (comboBox, resources, genService, addNone);
    }

    /**
     * Fills a given <code>JComboBox</code> with the map loop resources (instances
     * of <code>Resource</code>) provided by a <code>GeneralService</code>.
     * @param comboBox The <code>ComboBox</code> to be filled with instances of
     * <code>Resource</code>.  These are map loop resources.
     * @param genService A <code>GeneralService</code> that can provide the
     * map loop instances of <code>Resource</code>.
     * @param addNone True if "None" should be a listed option, false otherwise.
     */
    public static void initMapLoopComboBox (JComboBox<ResourceListCellItem> comboBox,
                                           GeneralService genService,
                                           boolean addNone) {
        // Get resources.
        Vector<Resource> resources = genService.getWeatherMapLoopResources();
        initResourceComboBox (comboBox, resources, genService, addNone);
    }

    /**
     * Fills a given <code>JComboBox</code> with the weather station resources
     * (instances of <code>Resource</code>) provided by a <code>GeneralService</code>.
     * @param comboBox The <code>ComboBox</code> to be filled with instances of
     * <code>Resource</code>.  These are weather station resources.
     * @param genService A <code>GeneralService</code> that can provide the
     * weather station instances of <code>Resource</code>.
     * @param addNone True if "None" should be a listed option, false otherwise.
     */
    public static void initWeatherStationComboBox (JComboBox<ResourceListCellItem> comboBox,
                                           GeneralService genService,
                                           boolean addNone) {
        // Get resources.
        Vector<Resource> resources = genService.getWeatherStationResources();
        initResourceComboBox (comboBox, resources, genService, addNone);
    }

    public static ComboBoxModel initHourlyTimeComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (HourlyTimeType t : HourlyTimeType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }
    /**
     * This method is to display all value of the enumeration in Barometric pressure 
     * trend combo box in Daily note tab of Notes and Daily Panel Manager. 
     * @return The ComboBoxModel that displays the Barometric pressure trend.
     */
    public static ComboBoxModel initBarometricPressureTrendComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (BarometricPressureTrendType t : BarometricPressureTrendType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }
    /**
     * This method is to display all value of the enumeration in temperature 
     * trend combo box in Daily note tab of Notes and Daily Panel Manager. 
     * @return The ComboBoxModel that displays the temperature trend.
     */
    public static ComboBoxModel initTempTrendComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (TemperatureTrendType t : TemperatureTrendType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }
    /**
     * This method is to display all value of the enumeration in Dew point 
     * trend combo box in Daily note tab of Notes and Daily Panel Manager 
     * @return The ComboBoxModel that displays the dew point trend.
     */
    public static ComboBoxModel initDPTrendComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (DewPointTrendType t : DewPointTrendType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }
    /**
     * This method is to display all value of the enumeration in Relative humidity 
     * trend combo box in Daily note tab of Notes and Daily Panel Manager 
     * @return The ComboBoxModel that displays the relative humidity trend.
     */
    public static ComboBoxModel initRHTrendComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (RelativeHumidityTrendType t : RelativeHumidityTrendType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }

    public static ComboBoxModel initCloudComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (CloudType t : CloudType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }

    public static ComboBoxModel initWindDirectionSummaryComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (WindDirectionSummaryType t : WindDirectionSummaryType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }

    public static ComboBoxModel initWindSpeedComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (WindSpeedType t : WindSpeedType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }

    public static ComboBoxModel initWindDirectionComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (WindDirectionType t : WindDirectionType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }

    public static ComboBoxModel initYesNoComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (YesNoType t : YesNoType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }

    public static ComboBoxModel initPressureChangeComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (PressureChangeRelationType t : PressureChangeRelationType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }

    public static ComboBoxModel initPrecipitationChanceComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (PrecipitationChanceType t : PrecipitationChanceType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }

    public static ComboBoxModel initTemperaturePredictionComboBoxModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

        for (TemperaturePredictionType t : TemperaturePredictionType.values()) {
            model.addElement(t.displayString());
        }

        return model;
    }
}
