package weather.clientside.gui.administrator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import weather.GeneralService;
import weather.clientside.utilities.BarebonesBrowser;
import weather.common.data.RetrievalMethod;
import weather.common.data.resource.*;
import weather.common.gui.component.IconProperties;
import weather.common.servercomm.StorageControlSystemImpl;
import weather.common.utilities.Debug;
import weather.common.utilities.PropertyManager;
import weather.common.utilities.WeatherException;

/**
 * Panel for use with adding and editing resources. This panel holds all of the
 * options that should be editable for the three main resource types. It can be
 * used for any of the three, to maintain consistency and modularity.
 *
 * @author Bloomsburg University Software Engineering
 * @author Paul Zipko(2007)
 * @author Mike Graboske (2008)
 * @author David Reichert (2008)
 * @author Ora Merkel (2009)
 * @author Eric Subach (2010)
 * @author Bingchen Yan (2012)
 * @author Zach Rothweiler
 * @version Spring 2012
 */
public class ResourceSettingsPanel extends javax.swing.JPanel {
    
    public final static String NEW_RESOURCE_ENTRY = "Add a new resource...";

    private final static int MINIMUM_FREQUENCY = 1;
    private ArrayList<String> storageSystemFolderList;
    private Resource currentResource;
    private Resource emptyResource;
    private Resource linkedStation;
    private weather.GeneralService generalService;
    private JDialog parent;
    private Calendar dateInitiated;
    private WeatherResourceType panelType;
    private boolean urlVerified = false;    //Has user verified URL?
    /**
     * The HashMap used to associate the resource folder with the resource.
     */
    private HashMap<String, String> resourceFolderMap;

    /**
     * Creates a new
     * <code>ResourceSettingsPanel</code> and defaults the display to the given
     * <code>Resource</code>. If a null resource is given, defaults to adding a
     * new resource.
     *
     * @param parent The parent dialog to this dialog.
     * @param type The
     * <code>WeatherResourceType</code> of the given
     * <code>Resource</code>.
     * @param generalService The
     * <code>GeneralService</code> object used by the client.
     */
    public ResourceSettingsPanel(JDialog parent, WeatherResourceType type,
            GeneralService generalService) {
        this.parent = parent;
        this.panelType = type;
        this.generalService = generalService;
        this.resourceFolderMap = new HashMap<>();
        this.urlVerified = false;
        this.linkedStation = null;

        initComponents();
        initFormatBox();
        initTimeZoneBox();
        initStationsList();
        initInstalledItemsList();

        collectionGroup.add(specifiedTimesOption);
        collectionGroup.add(daylightHoursOption);
        collectionGroup.add(fullTimeOption);
        
        timeZoneBox.setSelectedItem(ResourceTimeZone.getLocalDisplayString());
        
        this.currentResource = buildEmptyResource();

        //Connect specifiedTimesPanel to "Specified Hours" button.
        specifiedTimesPanel.setVisible(specifiedTimesOption.isSelected());
        specifiedTimesOption.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (collectionGroup.isSelected(specifiedTimesOption.getModel())) {
                    specifiedTimesPanel.setVisible(true);
                } else {
                    specifiedTimesPanel.setVisible(false);
                    startTimeList.setSelectedIndex(0);
                    endTimeList.setSelectedIndex(0);
                }
            }
        });

        //customize appearance and behavior based on panel type.
        stationLinkPanel.setVisible(panelType == WeatherResourceType.WeatherCamera);
        updateHourPanel.setVisible(panelType != WeatherResourceType.WeatherStationValues);
        timeZoneBox.setEnabled(panelType != WeatherResourceType.WeatherMapLoop);
        timeZoneLabel.setEnabled(panelType != WeatherResourceType.WeatherMapLoop);
        
        //Never show video dimensions when no existing resource is selected.
        videoSizePanel.setVisible(false);
        
        //Set up button to reset dimensios.
        webDimButton.setIcon(IconProperties.getGlobeIconImage());
        webDimButton.setToolTipText("Set the input fields to the size of "
                + "the current Internet image from rhe last saved URL.");
        
        emptyResource = buildEmptyResource();
    }
    
    /**
     * Attach listener to main drop down list.
     */
    public void attachListener(){
        installedItemsList.addItemListener(new ResourceChangeListener());
    }

    /**
     * Initializes the list of items that matches the resource type for this
     * panel.
     */
    private void initInstalledItemsList() {
        //for adding new items
        installedItemsList.addItem(NEW_RESOURCE_ENTRY);
        
        //for editing existing items 
        Vector<Resource> installedItems = getRelatedResources();
        for (Resource resource : installedItems) {
            installedItemsList.addItem(resource.getResourceName());
        }
    }

    /**
     * Initializes the list of formats available for each type of resource.
     */
    private void initFormatBox() {
        for (ResourceFileFormatType type : ResourceFileFormatType.values()) {
            //station should only have text, CSV, and SSV formats available.
            if(panelType.equals(WeatherResourceType.WeatherStationValues)
                    && (type.equals(ResourceFileFormatType.comma_separated_values)
                    || type.equals(ResourceFileFormatType.space_separated_values)
                    || type.equals(ResourceFileFormatType.text))){
                formatComboBox.addItem(type.toString());
            }
            else
                //Cameras & Maps should only have image formats available.
                if((panelType.equals(WeatherResourceType.WeatherCamera)
                        || panelType.equals(WeatherResourceType.WeatherMapLoop))
                        && (type.equals(ResourceFileFormatType.jpeg)
                        || type.equals(ResourceFileFormatType.gif)
                        || type.equals(ResourceFileFormatType.png)
                        || type.equals(ResourceFileFormatType.image))){
                    formatComboBox.addItem(type.toString());
                }
            
            if(type.equals(ResourceFileFormatType.unknown)) {
                formatComboBox.addItem(type.toString());
            }
        }
    }

    /**
     * Initializes the list of time zones available.
     */
    private void initTimeZoneBox() {

        for (ResourceTimeZone timezone : ResourceTimeZone.values()) {
            timeZoneBox.addItem(timezone.displayString());
        }

    }

    /**
     * Initializes the list of weather stations that can be linked to the
     * weather cameras.
     */
    private void initStationsList() {
        if (generalService != null) {
            availableWeatherStations.addItem("None");
            for (Resource weatherStation : generalService.getWeatherStationResources()) {
                availableWeatherStations.addItem(weatherStation.getResourceName());
            }
        }
    }

    /**
     * Sets the current resource to the given resource and displays the settings
     * for the given resource..
     *
     * @param r The resource the settings will be set to.
     * <code>Resource</code> to display and use.
     */
    public void setResource(Resource r) {
        installedItemsList.setSelectedItem(r.getResourceName());
        currentResource = r;
        if(currentResource.getResourceNumber() != -1) {
            populateFields();
        } else {
            clearFields();
        }
    }

    /**
     * Populates all of the settings fields using the current resource.
     */
    private void populateFields() {
        //Never show video dimensions if this is a weather station panel.
        videoSizePanel.setVisible(panelType != WeatherResourceType.WeatherStationValues);
        
        updateButton.setText("Update");
        errorLabel.setText("");
        if (currentResource != null) {
            latitudeField.setText("" + currentResource.getLatitude());
            longitudeField.setText("" + currentResource.getLongitude());
            resourceNameField.setText(currentResource.getResourceName());

            java.sql.Date date = currentResource.getDateInitiated();
            dateInitiated = new GregorianCalendar();
            dateInitiated.setTime(date);
            dateInitiatedLabel.setText("Date Initiated: "
                    + (dateInitiated.get(Calendar.MONTH) + 1) + "/"
                    + dateInitiated.get(Calendar.DAY_OF_MONTH) + "/"
                    + dateInitiated.get(Calendar.YEAR));

            formatComboBox.setSelectedItem(currentResource.getFormat().toString());
            resourceNumberValueLabel.setText("" + currentResource.getResourceNumber());
            activeCheckBox.setSelected(currentResource.isActive());
            visibleCheckBox.setSelected(currentResource.isVisible());
            timeZoneBox.setSelectedItem(currentResource.getTimeZone().displayString());
            storageFolderField.setText(currentResource.getStorageFolderName());
            urlTextField.setText(currentResource.getURL().toString());
            urlVerified = true; //Existing resource has verified URL
            downloadFrequencyField.setText("" + currentResource.getFrequency());
            downloadFrequencyResolution.setSelectedIndex(0);

            if (panelType == WeatherResourceType.WeatherCamera) {
                linkedStation = generalService.getDBMSSystem().getResourceRelationManager()
                        .getRelatedStationResource(currentResource);
                
                if(linkedStation!=null){
                    availableWeatherStations.setSelectedItem(linkedStation.getResourceName());
                } else {
                    availableWeatherStations.setSelectedIndex(0);
                }
            }

            switch (currentResource.getCollectionSpan()) {
                case DaylightHours:
                    collectionGroup.setSelected(
                            daylightHoursOption.getModel(), true);
                    endTimePanel.setVisible(false);
                    startTimePanel.setVisible(false);
                    break;
                case FullTime:
                    collectionGroup.setSelected(fullTimeOption.getModel(),
                            true);
                    endTimePanel.setVisible(false);
                    startTimePanel.setVisible(false);
                    break;
                case SpecifiedTimes:
                    collectionGroup.setSelected(
                            specifiedTimesOption.getModel(), true);
                    endTimePanel.setVisible(true);
                    startTimePanel.setVisible(true);
                    break;
            }
            
            //All resources have start and end hours; they are just 0 if a 
            //resourse does not use the "specified time" option.
            startTimeList.setSelectedIndex(currentResource.getStartTime());
            endTimeList.setSelectedIndex(currentResource.getEndTime());
            
            //All resources have video dimensions; they are just hidden (and 0)
            //if the resource is a weather station.
            videoWidthField.setText("" + currentResource.getImageWidth());
            videoHeightField.setText("" + currentResource.getImageHeight());
            
            //All resources have an update hour; it is just hidden (and 
            //-1/Never) if the resource is a weather station.  Moreover, the 
            //selected index should be one more than the value stored in the
            //resource.
            updateHourList.setSelectedIndex(currentResource.getUpdateHour() + 1);
        } //End of null test
    }

    /**
     * Returns a list of resources of the same type as used by the panel.
     *
     * @return A list of resources of the same type as used by the panel.
     */
    private Vector<Resource> getRelatedResources() {
        Vector<Resource> relatedResources = null;
        switch (panelType) {
            case WeatherCamera:
                relatedResources = generalService.getWeatherCameraResources();
                break;
            case WeatherStationValues:
                relatedResources = generalService.getWeatherStationResources();
                break;
            case WeatherMapLoop:
                relatedResources = generalService.getWeatherMapLoopResources();
                break;
        }
        return relatedResources;
    }

    /**
     * Clears all of the fields and sets them back to default values.
     */
    private void clearFields() {
        //Never show video dimensions when no existing resource is selected.
        videoSizePanel.setVisible(false);
        
        updateButton.setText("Add");
        errorLabel.setText("");
        resourceNumberValueLabel.setText("" + (-1));
        dateInitiated = new GregorianCalendar();
        dateInitiatedLabel.setText("Date Initiated: "
                + (dateInitiated.get(Calendar.MONTH) + 1) + "/"
                + dateInitiated.get(Calendar.DAY_OF_MONTH) + "/"
                + dateInitiated.get(Calendar.YEAR));

        latitudeField.setText("0");
        longitudeField.setText("0");
        resourceNameField.setText("");
        activeCheckBox.setSelected(false);
        visibleCheckBox.setSelected(false);
        formatComboBox.setSelectedIndex(0);
        timeZoneBox.setSelectedItem(ResourceTimeZone.getLocalDisplayString());
        storageFolderField.setText("");
        urlTextField.setText("http://");
        urlVerified = false;    //New resource does not have URL.
        collectionGroup.setSelected(fullTimeOption.getModel(), true);
        downloadFrequencyField.setText("0");
        downloadFrequencyResolution.setSelectedIndex(0);
        availableWeatherStations.setSelectedIndex(0);
        videoWidthField.setText("0");
        videoHeightField.setText("0");
        updateHourList.setSelectedIndex(0);
        currentResource = buildEmptyResource();
    }

    /**
     * Updates the combobox
     */
    public void updateComboBox() {
        Vector<Resource> installedItems = getRelatedResources();
        installedItemsList.removeAllItems();
        availableWeatherStations.removeAllItems();
        
        //updates Resource ComboBox
        for (Resource r : installedItems) {
            installedItemsList.addItem(r.getResourceName());
        }
        installedItemsList.insertItemAt(NEW_RESOURCE_ENTRY, 0);
        
        //updates Available Weather Stations
        this.initStationsList();
        
        //load resure data
        Vector<Resource> relatedResources = getRelatedResources();
        for (Resource r : relatedResources) {
            if (r.getResourceName().equals(installedItemsList.getSelectedItem())) {
                setResource(r);
                return;
            }
        }
        setResource(emptyResource);
        clearFields();
        
        
    }

    /**
     * A <code>ResourceChangeListener</code> to force the panel to load a
     * different resource when one is selected from the combo box.
     */
    class ResourceChangeListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Vector<Resource> relatedResources = getRelatedResources();
                for (Resource r : relatedResources) {
                    if (r.getResourceName().equals(e.getItem())) {
                        setResource(r);
                        return;
                    }
                }
                setResource(emptyResource);
            }
        }
    }
    
    /**
     * For use before listener is attached to prepare form for editing.
     */
    public void setToEdit(){
        installedItemsList.setSelectedItem(currentResource.getResourceName());
        if (currentResource.getResourceNumber() != -1) {
            updateButton.setText("Update");
        }
    }

    /**
     * Creates a
     * <code>Resource</code> object from the fields.
     *
     * @return A
     * <code>Resource</code> object creates from the fields.
     */
    private Resource buildResource() {
        Resource r;
        try {
            r = new Resource();
            r.setActive(activeCheckBox.isSelected());
            r.setVisible(visibleCheckBox.isSelected());
            r.setFormat(ResourceFileFormatType.enumFromString((String) formatComboBox.getSelectedItem()));
            r.setLatitude(Float.parseFloat(latitudeField.getText()));
            r.setLongitude(Float.parseFloat(longitudeField.getText()));
            r.setResourceName(resourceNameField.getText());
            r.setResourceNumber(Integer.parseInt(resourceNumberValueLabel.getText()));
            r.setResourceType(panelType);
            r.setStorageFolderName(storageFolderField.getText());
            r.setTimeZone(ResourceTimeZone.getEnum((String) timeZoneBox.getSelectedItem()));
            r.setURL(new URL(urlTextField.getText()));
            r.setDateInitiated(new java.sql.Date(dateInitiated.getTimeInMillis()));
            r.setStartTime(startTimeList.getSelectedIndex());
            r.setEndTime(endTimeList.getSelectedIndex());

            //Access method is based on panel type.
            if (panelType.equals(WeatherResourceType.WeatherCamera)
                    || panelType.equals(WeatherResourceType.WeatherMapLoop)) {
                r.setAccessMethod(RetrievalMethod.url);
            } else if (panelType.equals(WeatherResourceType.WeatherStationValues)) {
                r.setAccessMethod(RetrievalMethod.fileload);
            } else {
                r.setAccessMethod(RetrievalMethod.undefined);
            }

            //figure out collection span
            if (collectionGroup.getSelection().equals(fullTimeOption.getModel())) {
                r.setCollectionSpan(ResourceCollectionSpan.FullTime);
            } else if (collectionGroup.getSelection().equals(specifiedTimesOption.getModel())) {
                r.setCollectionSpan(ResourceCollectionSpan.SpecifiedTimes);
            } else {
                r.setCollectionSpan(ResourceCollectionSpan.DaylightHours);
            }

            int frequencyValue = Integer.parseInt(downloadFrequencyField.getText());
            if (downloadFrequencyResolution.getSelectedItem().equals("Minutes")) {
                r.setFrequency(frequencyValue * 60);
            } else if (downloadFrequencyResolution.getSelectedItem().equals("Hours")) {
                r.setFrequency(frequencyValue * 60 * 60);
            } else {
                r.setFrequency(frequencyValue);
            }
            
            r.setImageWidth(Integer.parseInt(videoWidthField.getText()));
            r.setImageHeight(Integer.parseInt(videoHeightField.getText()));
            r.setUpdateHour(updateHourList.getSelectedIndex() - 1);
            
            //Copy field that can't be changed from the current resouce.
            r.setOrderRank(currentResource.getOrderRank());
        } catch (Exception ex) {
            r = null;
        }
           
        return r;
    }
    
    /**
     * Builds an empty resource to match the "Add a New" option.
     * @return The empty resource.
     */
    private Resource buildEmptyResource(){
        Resource r = new Resource();
        r.setResourceName(NEW_RESOURCE_ENTRY);
        r.setResourceType(panelType);
        r.setTimeZone(ResourceTimeZone.getEnum(ResourceTimeZone
                .getLocalDisplayString()));
        
        //Access method is based on panel type.
        if (panelType.equals(WeatherResourceType.WeatherCamera)
                || panelType.equals(WeatherResourceType.WeatherMapLoop)) {
            r.setAccessMethod(RetrievalMethod.url);
        } else if (panelType.equals(WeatherResourceType.WeatherStationValues)) {
            r.setAccessMethod(RetrievalMethod.fileload);
        } else {
            r.setAccessMethod(RetrievalMethod.undefined);
        }
        
        r.setCollectionSpan(ResourceCollectionSpan.FullTime);
        r.setFormat(ResourceFileFormatType.enumFromString((String) 
                formatComboBox.getItemAt(0)));
        r.setStorageFolderName("");
        try {
            r.setURL(new URL("http://"));
        } catch (MalformedURLException ex) {
            Debug.println("Bad URL in empty resource.");
            r.setURL(null);
        }
        r.setFrequency(0);
        return r;
    }
    
    /**
     * Looks on the Internet to find the dimensions of the images currently 
     * being produced by a current or yet-to-be-saved <code>Resource</code>.
     * 
     * NOTE: This should only be called if the <code>Resource</code> makes 
     * videos.
     * 
     * @param resource The new <code>Resource</code>, which may have yet to be
     * saved.
     * @return The dimensions in a <code>Dimension</code> object, which will
     * have width and height of zero if an error occurs.
     */
    private Dimension getCurrentResourceDimension(Resource resource) {
        //Set defalt values in case of an error
        int width = 0;
        int height = 0;
        
        ImageInstance instance = new ImageInstance(resource);
        
        try {
            instance.readURL(resource.getURL());

            BufferedImage img = (BufferedImage) instance.getImage();
            width = img.getWidth();
            height = img.getHeight();
        } catch (ConnectException | SocketTimeoutException 
                | WeatherException ex) {
            //No work needed.
            Debug.println(ex.getMessage());
        }
        
        return new Dimension(width, height);
    }

    /**
     * Determines if there have been new changes to the
     * <code>Resource</code>
     *
     * @return True if there have been new changes, false otherwise.
     */
    public boolean newChanges() { 
        /**
         * We must create a resource to compare against to see if there are any
         * changes.  This can be a copy of the current resource except that we
         * must clear the resource name if it equals NEW_RESOURCE_ENTRY.
         * Otherwise, the test for changes won't work.
         */ 
        Resource resourceToCompareAgainst = new Resource(currentResource);
        if (resourceToCompareAgainst.getResourceName()
                .equals(NEW_RESOURCE_ENTRY)) {
            resourceToCompareAgainst.setResourceName("");
        }
        
        //If a resource can't be built, their have been changes.
        Resource shownResource = buildResource();
        if (shownResource == null) {
            return true;
        }
        
        if(!(resourceToCompareAgainst.identical(shownResource))) {
            return true;
        }

        if (panelType == WeatherResourceType.WeatherCamera) {
            Resource linkedResource = generalService.getDBMSSystem()
                    .getResourceRelationManager()
                    .getRelatedStationResource(currentResource);
            String resourceName = linkedResource == null 
                    ? "None" : linkedResource.getResourceName();
            if(!resourceName.equals(availableWeatherStations
                    .getSelectedItem())){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether the frequency given is valid.
     *
     * @return True if the frequency is valid, false otherwise.
     */
    private boolean validateFrequency() {
        errorLabel.setForeground(Color.red);
        String frequency = downloadFrequencyField.getText();

        if (frequency.equals("")) {
            errorLabel.setText("You must enter a frequency for the resource.");
            return false;
        }
        try {
            if (Integer.valueOf(frequency).intValue()
                    < MINIMUM_FREQUENCY) {
                errorLabel.setText("Enter a download frequency greater than "
                        + MINIMUM_FREQUENCY + ".");
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Enter a integer for frequency.");
            return false;
        }

        return true;
    }

    /**
     * Determines whether the resource name given is valid.
     *
     * @return True if the name is valid, false otherwise.
     */
    private boolean validateResourceName() {
        errorLabel.setForeground(Color.red);
        String resourceName = resourceNameField.getText();
        String selectedName = (String) installedItemsList.getSelectedItem();
        
        if(resourceName.contains(",")) {
            errorLabel.setText("Resource names cannot have commas.");
            return false;
        }
        if (resourceName.trim().equals("")) {
            errorLabel.setText("You must enter a name for the resource.");
            return false;
        }

        if (resourceName.equals(selectedName)) {
            return true;
        }

        for (Resource res : generalService.getDBMSSystem().getResourceManager().getResourceList()) {
            if (res.getName().equals(resourceName)) {
                errorLabel.setText("You must enter a unique resource name.");
                return false;
            }
        }

        if (!validateCharacters(resourceName, "resource")) {
            return false;
        }

        return true;
    }
    
    /**
     * Helper function to show the URL has been verified.
     */
    private void showURLVerified(){
        errorLabel.setText("");
        JOptionPane pane = new JOptionPane("The URL has been verified.",
                JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog("URL verified");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Helper function to show the URL has not been verified.
     */
    private void showURLNotVerified() {
        JOptionPane pane = new JOptionPane("The URL has not been verified.",
                JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog("URL not verified");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Helper function to show the URL is not well-formed.
     */
    private void showBadURL() {
        errorLabel.setForeground(Color.red);
        errorLabel.setText("You must enter a valid URL.");
    }
    
    /**
     * Helper function to show that a resource has been added or not added.
     * 
     * @param success Whether or not the operation was successful.
     * @param dimSetError True if there was an error retrieving the image
     * dimensions of a video-making <code>Resource</code>, False otherwise.
     */
    private void showResourceAdded(boolean success, boolean dimSetError){
        String message = success ? "The resource has been added." :
                "The resource has NOT been added.";
        if (success && dimSetError) {
            message += "\nNOTE: Video dimensions could not be retrieved.";
        }
        String title = success ? "Resource Added" : "Resource NOT Added";
        JOptionPane pane = new JOptionPane(message,
                JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Helper function to show that a resource has been edited or not edited.
     * 
     * @param success Whether or not the operation was successful.
     */
    private void showResourceEdited(boolean success){
        String message = success ? "The resource has been edited." :
                "The resource has NOT been edited.";
        String title = success ? "Resource Edited" : "Resource NOT Edited";
        JOptionPane pane = new JOptionPane(message,
                JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Function to check before saving
     */
    private void checkURLForSave(){
        if (urlVerified) {
            return;
        }
        if (!this.isValidURL()) {
            this.showBadURL();
            return;
        }
        if(!this.promptToTestURL()){
            //user is happy that URL is good
            urlVerified = true;
            return;
        }
        if (this.doesUserApproveURL()) {
            urlVerified = true;
            this.showURLVerified();
        } else {
            this.showURLNotVerified();
            errorLabel.setForeground(Color.red);
            errorLabel.setText("A resource cannot be saved with an unverified URL.");
        }
    }
    
    /**
     * Helper function to ask the user to verify the URL.  It opens the entered
     * URL and asks the user for verification,
     * @return The user's verdict on the URL.
     */
    private boolean doesUserApproveURL() {
        BarebonesBrowser.openURL(urlTextField.getText().trim(), this);
        JOptionPane pane = new JOptionPane("Has the URL opened correctly?", JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog("Test URL");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return false;
        }
        if (selectedValue instanceof Integer) {
            int intValue = ((Integer) selectedValue).intValue();
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }
    
    /**
     * Asks the user if he or she is sure about the selection of a time zone for
     * a new resource.  (Designed to be called when saving a new camera or
     * weather station.)
     * @return True if the user selects "Yes," false otherwise.
     */
    private boolean proceedWithFirstTimeZone() {
        String message = 
                "Are you sure you want to create this resource with the selected\n"
                + "time zone?  Data collection is based on the time zone and is\n"
                + "difficult to change later.";
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog("Please Verify Time Zone");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return false;
        }
        if (selectedValue instanceof Integer) {
            int intValue = ((Integer) selectedValue).intValue();
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }
    
    /**
     * Asks the user if he or she wants tho proceed with changing a resource's
     * time zone without changing its storage folder.  (Designed to be called 
     * when saving an existing camera or weather station if the user has changed
     * the time zone but not the storage folder.)
     * @return True if the user selects "Yes," false otherwise.
     */
    private boolean proceedWithNewTimeZoneInOldFolder() {
        String message =
                "You are about to change the time zone without changing the\n"
                + "storage folder.  It is not recommended to do this as it can\n"
                + "lead to inconsistent data retrieval and problems with any data\n"
                + "already collected.  Instead, it is recommended that you pick a\n"
                + "new storage folder.  Do you want to proceed anyway?";
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog("Please Verify Time Zone and Storage"
                + " Folder");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return false;
        }
        if (selectedValue instanceof Integer) {
            int intValue = ((Integer) selectedValue).intValue();
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }

    /**
     * Helper function to check the syntax of a URL.
     *
     * @return Whether or not the syntax of the URL is valid.
     */
    private boolean isValidURL() {
        String URLField = urlTextField.getText().trim();
        if(URLField.equals("http://") || URLField.equals("")) {
            return false;
        }
        URL u;
        try {
            u = new URL(urlTextField.getText().trim());
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the user wants to test the URL.
     * @return Whether or not the user wants to test the URL.
     */
    private boolean promptToTestURL() {
        JOptionPane pane = new JOptionPane("<html>Would you like to test the URL?" +
                "<br/>(\"No\" will automatically verify the URL.)</html>", 
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog("Test URL?");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return false;
        }
        if (selectedValue instanceof Integer) {
            int intValue = ((Integer) selectedValue).intValue();
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }

    /**
     * Checks if the user wants reset the data fields.
     * @return Whether or not the user wants to test the URL.
     */
    private boolean promptToReset() {
        JOptionPane pane = new JOptionPane("Are you sure you want to reset " +
                "the fields?", 
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog("Reset Fields?");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return false;
        }
        if (selectedValue instanceof Integer) {
            int intValue = ((Integer) selectedValue).intValue();
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }
    
    /**
     * Checks if the user wants to use an old, unused folder.
     * @return Whether or not the user wants to test the URL.
     */
    private boolean promptUseOldFolder() {
        JOptionPane pane = new JOptionPane("<html>" +
                "You have choosen to use an old storage folder that is<br>" +
                "currently not in use.<br/>Are you sure you want to do this?</html>", 
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog("Use Old Folder?");
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return false;
        }
        if (selectedValue instanceof Integer) {
            int intValue = ((Integer) selectedValue).intValue();
            return intValue == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }
    
    /**
     * Validates the fields. (URL checked elsewhere)
     *
     * @return True if the fields appear valid, false otherwise.
     */
    private boolean validateFields() {
        errorLabel.setText("");

        if (!validateResourceName()) {
            return false;
        }
        
        if (!validateCoordinates()) {
            return false;
        }
        
        if (!validateStorageFolder(true)) {
            return false;
        } else {
            //Clear message of valid folder name.
            errorLabel.setText("");
        }
        
        if (!validateFrequency()) {
            return false;
        }
        
        if (!validateTimeRange()) {
            return false;
        }
            
        if (!validateVideoDimensions()) {
            return false;
        }
        
        return true;
    }

    /**
     * Determines whether the characters in the given resource or folder name are valid.
     *
     * @param field The value to test.
     * @param type The type of name to be checked.
     * @return True if the characters are valid, false otherwise.
     */
    private boolean validateCharacters(String field, String type) {
        errorLabel.setForeground(Color.red);
        Pattern regex = Pattern.compile("[\\\\\\/\\:\\*\\?@\\<\\>]");
        Matcher check = regex.matcher(field);

        if (check.find()) {
            errorLabel.setText("<html>You have an invalid "
                    + "character in your " + type + " name.<br/>You can not use "
                    + "any of the following characters: \\ / : * ? @ &lt; &gt;."
                    + "</html>");
            return false;
        }
        return true;
    }
    
    /**
     * Validates the Coordinates
     * @return True if the coordinates are valid, false otherwise.
     */
    private boolean validateCoordinates() {
        errorLabel.setForeground(Color.red);
        try{
            float latitude = Float.parseFloat(latitudeField.getText());
            float longitude = Float.parseFloat(longitudeField.getText());
            
            //checks for acceptable range for each corrdinate
            if(!(latitude>=-90 && latitude<=90)
                    || !(longitude>=-180 && longitude<=180)) {
                errorLabel.setText("Latitudes range from -90 to 90.\n"
                        + "Longitudes range from -180 to 180.");
                return false;
            }   
        }
        //this is thrown if the fields have letters or are empty
        catch (NumberFormatException e){
            errorLabel.setText("Coordinates need to be as decimal degrees (ddd.ddddd) and cannot be empty.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates the Video Dimensions and, in some cases, asks the user if 
     * dimensions of zero should be kept.  The user is not asked about zeros
     * when the dimensions are not visible.  If that case zeros are valid.
     * Otherwise, the user determines if they are.
     * @return True if the dimensions are valid, false otherwise.
     */
    private boolean validateVideoDimensions() {
        //If dimensions are not visible, they are valid zeros.
        if (!videoSizePanel.isVisible()) {
            return true;
        }
        
        errorLabel.setForeground(Color.red);
        int videoWidth;
        int videoHeight;
        try{
            videoWidth = Integer.parseInt(videoWidthField.getText());
            videoHeight = Integer.parseInt(videoHeightField.getText());
            
            //Make sure dimensions are not negitive.
            if(videoWidth < 0 || videoHeight < 0) {
                errorLabel.setText("Video dimensions cannot be negitive.");
                return false;
            }   
        }
        //This is thrown if the fields have letters or are empty.
        catch (NumberFormatException e){
            errorLabel.setText("Video dimensions must be integers.");
            return false;
        }
        
        //Check for visible zeros.
        if (videoWidth == 0 || videoHeight == 0) {
            String message =
                    "Keeping video dimensions of 0 will cause the program to\n"
                    + "check the Internet for image dimensions the next time\n"
                    + "it makes a video for this resource.  Do you want to\n"
                    + "keep any dimemsions of 0?";
            String title = "Confirm Video Dimensions";
            boolean result;
            JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_OPTION);
            JDialog dialog = pane.createDialog(title);
            dialog.setIconImage(IconProperties.getTitleBarIconImage());
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
            Object selectedValue = pane.getValue();
            if (selectedValue == null) {
                result = false;
            }
            if (selectedValue instanceof Integer) {
                int intValue = ((Integer) selectedValue).intValue();
                result = intValue == JOptionPane.YES_OPTION;
            } else {
                result = false;
            }
            
            return result;
        }
        
        //Dimensions are positive integers.
        return true;
    }
    
    /**
     * Makes sure the collection range start time is before its end time.  Note
     * that 12 a.m. is the start and end of the day.
     * @return True if the collection range start time is before its end time;
     * False otherwise.
     */
    private boolean validateTimeRange() {
        errorLabel.setForeground(Color.red);
        //Any option is after 12 a.m. (start of day) or before 12 a.m. (end of 
        //day).
        if(startTimeList.getSelectedIndex() == 0
                || endTimeList.getSelectedIndex() == 0) {
            return true;
        }
        
        //Check for all other correct combinations.
        if(startTimeList.getSelectedIndex() < endTimeList.getSelectedIndex()) {
            return true;
        }
        
        //If code gets here, the setting is incorrect.
        errorLabel.setText("The collection range start time must be before its end time.");
        return false;
    }

    /**
     * Contacts the database server and retrieves the resource bound to each
     * folder. These values are added to
     * <code>resourceFolderMap</code>.
     *
     * @param storageSystemFolderList The list of folders on the storage system.
     */
    private void retrieveResourceBindings(ArrayList<String> storageSystemFolderList) {
            
        Collection<Resource> resourceList = generalService.getDBMSSystem().
                getResourceManager().getResourceList();
        
        for(Resource r: resourceList){
            if(storageSystemFolderList.contains(r.getStorageFolderName())){
                resourceFolderMap.put(r.getStorageFolderName(),r.getResourceName());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        collectionGroup = new javax.swing.ButtonGroup();
        contentPanel = new javax.swing.JPanel();
        installedItemsList = new javax.swing.JComboBox<>();
        resourceNumberPanel = new javax.swing.JPanel();
        resourceNumberValueLabel = new javax.swing.JLabel();
        resourceNumberLabel = new javax.swing.JLabel();
        resourceNamePanel = new javax.swing.JPanel();
        resourceNameLabel = new javax.swing.JLabel();
        resourceNameField = new javax.swing.JTextField();
        dateInitiatedAndCheckBoxPanel = new javax.swing.JPanel();
        dateInitiatedLabel = new javax.swing.JLabel();
        activeCheckBox = new javax.swing.JCheckBox();
        visibleCheckBox = new javax.swing.JCheckBox();
        formatPanel = new javax.swing.JPanel();
        formatLabel = new javax.swing.JLabel();
        formatComboBox = new javax.swing.JComboBox<>();
        timeZonePanel = new javax.swing.JPanel();
        timeZoneLabel = new javax.swing.JLabel();
        timeZoneBox = new javax.swing.JComboBox<>();
        timeZoneMapButton = new javax.swing.JButton();
        coordinatePanel = new javax.swing.JPanel();
        latitudeLabel = new javax.swing.JLabel();
        latitudeField = new javax.swing.JTextField();
        longitudeLabel = new javax.swing.JLabel();
        longitudeField = new javax.swing.JTextField();
        googleMapsButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        storageFolderPanel = new javax.swing.JPanel();
        storageFolderLabel = new javax.swing.JLabel();
        storageFolderField = new javax.swing.JTextField();
        storageFolderBrowseButton = new javax.swing.JButton();
        urlPanel = new javax.swing.JPanel();
        urlLabel = new javax.swing.JLabel();
        urlTextField = new javax.swing.JTextField();
        testUrlButton = new javax.swing.JButton();
        donwloadFrequencyPanel = new javax.swing.JPanel();
        downloadFrequencyLabel = new javax.swing.JLabel();
        downloadFrequencyField = new javax.swing.JTextField();
        downloadFrequencyResolution = new javax.swing.JComboBox();
        collectionSpanPanel = new javax.swing.JPanel();
        collectionSpanLabel = new javax.swing.JLabel();
        fullTimeOption = new javax.swing.JRadioButton();
        daylightHoursOption = new javax.swing.JRadioButton();
        specifiedTimesOption = new javax.swing.JRadioButton();
        specifiedTimesPanel = new javax.swing.JPanel();
        startTimePanel = new javax.swing.JPanel();
        startTimeLabel = new javax.swing.JLabel();
        startTimeList = new javax.swing.JComboBox();
        endTimePanel = new javax.swing.JPanel();
        endTimeLabel = new javax.swing.JLabel();
        endTimeList = new javax.swing.JComboBox();
        timeHelpButton = new javax.swing.JButton();
        videoSizePanel = new javax.swing.JPanel();
        videoWidthLabel = new javax.swing.JLabel();
        videoWidthField = new javax.swing.JTextField();
        videoHeightLabel = new javax.swing.JLabel();
        videoHeightField = new javax.swing.JTextField();
        webDimButton = new javax.swing.JButton();
        updateHourPanel = new javax.swing.JPanel();
        updateHourTimePanel = new javax.swing.JPanel();
        updateHourLabel = new javax.swing.JLabel();
        updateHourList = new javax.swing.JComboBox();
        updateHourHelpButton = new javax.swing.JButton();
        stationLinkPanel = new javax.swing.JPanel();
        stationLinkLabel = new javax.swing.JLabel();
        availableWeatherStations = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        updateButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        contentPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        installedItemsList.setPreferredSize(new java.awt.Dimension(420, 25));
        contentPanel.add(installedItemsList);

        resourceNumberPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        resourceNumberPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        resourceNumberPanel.add(resourceNumberValueLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(99, 0, 89, 22));

        resourceNumberLabel.setLabelFor(resourceNumberValueLabel);
        resourceNumberLabel.setText("Resource Number:");
        resourceNumberPanel.add(resourceNumberLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 89, 22));

        contentPanel.add(resourceNumberPanel);

        resourceNamePanel.setPreferredSize(new java.awt.Dimension(420, 25));
        resourceNamePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        resourceNameLabel.setText("Resource Name:");
        resourceNamePanel.add(resourceNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 3, -1, -1));
        resourceNamePanel.add(resourceNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 0, 266, 20));

        contentPanel.add(resourceNamePanel);

        dateInitiatedAndCheckBoxPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        dateInitiatedAndCheckBoxPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dateInitiatedLabel.setText("Date Initiated:");
        dateInitiatedAndCheckBoxPanel.add(dateInitiatedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1, 220, 16));

        activeCheckBox.setText("Active");
        activeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                activeCheckBoxActionPerformed(evt);
            }
        });
        dateInitiatedAndCheckBoxPanel.add(activeCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 5, -1, -1));

        visibleCheckBox.setText("Visible");
        visibleCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visibleCheckBoxActionPerformed(evt);
            }
        });
        dateInitiatedAndCheckBoxPanel.add(visibleCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(319, 5, -1, -1));

        contentPanel.add(dateInitiatedAndCheckBoxPanel);

        formatPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        formatPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        formatLabel.setText("Format:");
        formatPanel.add(formatLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 3, -1, -1));

        formatPanel.add(formatComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(56, 0, 350, 20));

        contentPanel.add(formatPanel);

        timeZonePanel.setPreferredSize(new java.awt.Dimension(420, 25));
        timeZonePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        timeZoneLabel.setText("Time Zone:");
        timeZonePanel.add(timeZoneLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 4, -1, -1));

        timeZonePanel.add(timeZoneBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 1, 226, -1));

        timeZoneMapButton.setText("Time Zone Map");
        timeZoneMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeZoneMapButtonActionPerformed(evt);
            }
        });
        timeZonePanel.add(timeZoneMapButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(293, 0, 123, -1));

        contentPanel.add(timeZonePanel);

        coordinatePanel.setPreferredSize(new java.awt.Dimension(420, 25));
        coordinatePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        latitudeLabel.setText("Latitude:");
        coordinatePanel.add(latitudeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 4, -1, -1));

        latitudeField.setText("0");
        latitudeField.setToolTipText("");
        coordinatePanel.add(latitudeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(47, 1, 52, -1));

        longitudeLabel.setText("Longitude:");
        coordinatePanel.add(longitudeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 4, -1, -1));

        longitudeField.setText("0");
        coordinatePanel.add(longitudeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 1, 52, -1));

        googleMapsButton.setText("Google Maps");
        googleMapsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                googleMapsButtonActionPerformed(evt);
            }
        });
        coordinatePanel.add(googleMapsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(293, 0, 123, -1));

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        coordinatePanel.add(helpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 0, 57, -1));

        contentPanel.add(coordinatePanel);

        storageFolderPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        storageFolderPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        storageFolderLabel.setText("Storage Folder:");
        storageFolderPanel.add(storageFolderLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 4, -1, -1));

        storageFolderField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                storageFolderKeyReleased(evt);
            }
        });
        storageFolderPanel.add(storageFolderField, new org.netbeans.lib.awtextra.AbsoluteConstraints(79, 1, 209, -1));

        storageFolderBrowseButton.setText("Browse...");
        storageFolderBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                storageFolderBrowseButtonActionPerformed(evt);
            }
        });
        storageFolderPanel.add(storageFolderBrowseButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 0, 122, -1));

        contentPanel.add(storageFolderPanel);

        urlPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        urlPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        urlLabel.setText("URL:");
        urlPanel.add(urlLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 4, -1, -1));

        urlTextField.setText("http://");
        urlTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                urlFieldKeyReleased(evt);
            }
        });
        urlPanel.add(urlTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 1, 262, -1));

        testUrlButton.setText("Test URL");
        testUrlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testUrlButtonActionPerformed(evt);
            }
        });
        urlPanel.add(testUrlButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 0, 121, -1));

        contentPanel.add(urlPanel);

        donwloadFrequencyPanel.setOpaque(false);
        donwloadFrequencyPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        donwloadFrequencyPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        downloadFrequencyLabel.setText("Download Frequency (once every):");
        donwloadFrequencyPanel.add(downloadFrequencyLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 3, -1, -1));

        downloadFrequencyField.setText("0");
        donwloadFrequencyPanel.add(downloadFrequencyField, new org.netbeans.lib.awtextra.AbsoluteConstraints(174, 0, 62, -1));

        downloadFrequencyResolution.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seconds", "Minutes", "Hours", " " }));
        donwloadFrequencyPanel.add(downloadFrequencyResolution, new org.netbeans.lib.awtextra.AbsoluteConstraints(242, 0, 99, -1));

        contentPanel.add(donwloadFrequencyPanel);

        collectionSpanPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        collectionSpanPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        collectionSpanLabel.setText("Collection Span:");
        collectionSpanPanel.add(collectionSpanLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 4, -1, -1));

        fullTimeOption.setSelected(true);
        fullTimeOption.setText("Full Time");
        collectionSpanPanel.add(fullTimeOption, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 0, -1, -1));

        daylightHoursOption.setText("Daylight Hours");
        collectionSpanPanel.add(daylightHoursOption, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 0, -1, -1));

        specifiedTimesOption.setText("Specified Times");
        collectionSpanPanel.add(specifiedTimesOption, new org.netbeans.lib.awtextra.AbsoluteConstraints(269, 0, -1, -1));

        contentPanel.add(collectionSpanPanel);

        specifiedTimesPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        specifiedTimesPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        startTimePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        startTimeLabel.setText("Start Time:");
        startTimeLabel.setToolTipText("");
        startTimePanel.add(startTimeLabel);

        startTimeList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12 AM", "  1 AM", "  2 AM", "  3 AM", "  4 AM", "  5 AM", "  6 AM", "  7 AM", "  8 AM", "  9 AM", "10 AM", "11 AM", "12 PM", "  1 PM", "  2 PM", "  3 PM", "  4 PM", "  5 PM", "  6 PM", "  7 PM", "  8 PM", "  9 PM", "10 PM", "11 PM" }));
        startTimePanel.add(startTimeList);

        specifiedTimesPanel.add(startTimePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 1, -1, -1));

        endTimePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        endTimeLabel.setText("End Time:");
        endTimePanel.add(endTimeLabel);

        endTimeList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12 AM", "  1 AM", "  2 AM", "  3 AM", "  4 AM", "  5 AM", "  6 AM", "  7 AM", "  8 AM", "  9 AM", "10 AM", "11 AM", "12 PM", "  1 PM", "  2 PM", "  3 PM", "  4 PM", "  5 PM", "  6 PM", "  7 PM", "  8 PM", "  9 PM", "10 PM", "11 PM" }));
        endTimePanel.add(endTimeList);

        specifiedTimesPanel.add(endTimePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(133, 1, -1, -1));

        timeHelpButton.setText("Help With Times...");
        timeHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeHelpButtonActionPerformed(evt);
            }
        });
        specifiedTimesPanel.add(timeHelpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 0, 121, 23));

        contentPanel.add(specifiedTimesPanel);

        videoSizePanel.setPreferredSize(new java.awt.Dimension(420, 25));
        videoSizePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        videoWidthLabel.setText("<html><b>Dimensions of Produced Videos:</b>  Width:</html>");
        videoSizePanel.add(videoWidthLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 4, 213, 16));

        videoWidthField.setText("0");
        videoWidthField.setToolTipText("");
        videoSizePanel.add(videoWidthField, new org.netbeans.lib.awtextra.AbsoluteConstraints(218, 1, 52, 20));

        videoHeightLabel.setText("Height:");
        videoSizePanel.add(videoHeightLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(275, 4, 35, 16));

        videoHeightField.setText("0");
        videoSizePanel.add(videoHeightField, new org.netbeans.lib.awtextra.AbsoluteConstraints(315, 1, 52, 20));

        webDimButton.setLabel("");
        webDimButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                webDimButtonActionPerformed(evt);
            }
        });
        videoSizePanel.add(webDimButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(372, 1, 20, 20));

        contentPanel.add(videoSizePanel);

        updateHourPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        updateHourPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        updateHourTimePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        updateHourLabel.setText("Video Dimension Update Hour:");
        updateHourLabel.setToolTipText("");
        updateHourTimePanel.add(updateHourLabel);

        updateHourList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Never", "12 AM", "  1 AM", "  2 AM", "  3 AM", "  4 AM", "  5 AM", "  6 AM", "  7 AM", "  8 AM", "  9 AM", "10 AM", "11 AM", "12 PM", "  1 PM", "  2 PM", "  3 PM", "  4 PM", "  5 PM", "  6 PM", "  7 PM", "  8 PM", "  9 PM", "10 PM", "11 PM" }));
        updateHourTimePanel.add(updateHourList);

        updateHourPanel.add(updateHourTimePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 1, 245, 20));

        updateHourHelpButton.setText("What's This?");
        updateHourHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateHourHelpButtonActionPerformed(evt);
            }
        });
        updateHourPanel.add(updateHourHelpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 0, 121, 23));

        contentPanel.add(updateHourPanel);

        stationLinkPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        stationLinkPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        stationLinkLabel.setText("Linked Weather Station:");
        stationLinkPanel.add(stationLinkLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 3, -1, -1));

        stationLinkPanel.add(availableWeatherStations, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 0, 262, -1));

        contentPanel.add(stationLinkPanel);

        buttonPanel.setPreferredSize(new java.awt.Dimension(420, 25));
        buttonPanel.setLayout(new java.awt.GridLayout(1, 0, 50, 0));

        updateButton.setText("Add");
        updateButton.setMaximumSize(new java.awt.Dimension(65, 23));
        updateButton.setMinimumSize(new java.awt.Dimension(65, 23));
        updateButton.setPreferredSize(new java.awt.Dimension(65, 23));
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(updateButton);

        resetButton.setText("Reset");
        resetButton.setMaximumSize(new java.awt.Dimension(65, 23));
        resetButton.setMinimumSize(new java.awt.Dimension(65, 23));
        resetButton.setPreferredSize(new java.awt.Dimension(65, 23));
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(resetButton);

        cancelButton.setText("Close");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        contentPanel.add(buttonPanel);

        errorLabel.setForeground(new java.awt.Color(204, 0, 0));
        errorLabel.setPreferredSize(new java.awt.Dimension(370, 25));
        contentPanel.add(errorLabel);

        add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 420, 510));
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Displays a time zone map in a separate window.
     *
     * @param evt The event that triggers the action.
     */
    private void timeZoneMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeZoneMapButtonActionPerformed
        // Display the time zone map in an external program.
        String mapFilePath = IconProperties.getTimeZoneMapImagePath();
        // Verify Windows OS. (Show error message otherwise.)
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            try {
                Runtime.getRuntime()
                        .exec("rundll32 url.dll,FileProtocolHandler "
                                + mapFilePath);
            } catch (Exception e) {
                JOptionPane pane = new JOptionPane("Error attempting to launch"
                        + " time zone map viewer.",
                        JOptionPane.ERROR_MESSAGE);
                JDialog dialog = pane.createDialog("Error Showing Map");
                dialog.setIconImage(IconProperties.getTitleBarIconImage());
                dialog.setLocationRelativeTo(parent);
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
            }
        } else {
            // No a Windows OS.
            JOptionPane pane = new JOptionPane("This feature is only available"
                        + " on windows systems.",
                        JOptionPane.ERROR_MESSAGE);
                JDialog dialog = pane.createDialog("Feature Not Available");
                dialog.setIconImage(IconProperties.getTitleBarIconImage());
                dialog.setLocationRelativeTo(parent);
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
        }
    }//GEN-LAST:event_timeZoneMapButtonActionPerformed

    /**
     * Displays a list of folders from the storage system and the resources that
     * are associated with them.
     *
     * @param evt The event that triggers the action.
     */
    private void storageFolderBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storageFolderBrowseButtonActionPerformed
        if (storageSystemFolderList == null) {
            this.storageSystemFolderList = (StorageControlSystemImpl.getStorageSystem()).retrieveFolderList();
            retrieveResourceBindings(storageSystemFolderList);
        }
        new ResourceFolderWindow(resourceFolderMap);
    }//GEN-LAST:event_storageFolderBrowseButtonActionPerformed

    /**
     * Test to see if URL is verified.
     *
     * @param evt The event that triggers the action.
     */
    private void testUrlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testUrlButtonActionPerformed
        if(urlVerified) {
            this.showURLVerified();
            return;
        }
        if(!this.isValidURL()) {
            this.showBadURL();
            return;
        }
        if(this.doesUserApproveURL()) {
            urlVerified = true;
            this.showURLVerified();
        } else {
            this.showURLNotVerified();
        }
    }//GEN-LAST:event_testUrlButtonActionPerformed

    /**
     * Closes the window without saving changes. Checks if there have been
     * changes made to the form. If there have, it prompts the user to save the
     * changes. If the user does not save changes, the window is closed.
     * Otherwise, the window is left open.
     *
     * @param evt The event that triggers the action.
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        checkForParentClose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Checks if there have been changes made to the form. If there have, it
     * prompts the user to save the changes. If the user does not save changes,
     * the window is closed. Otherwise, the window is left open.
     */
    public void checkForParentClose() {
        boolean changesFound = newChanges();
        if (changesFound && generalService.leaveWithoutSaving(parent)) {
            parent.dispose();
        } else if (!changesFound) {
            parent.dispose();
        }
    }
    
    /**
     * Determines whether on not it is OK to leave this panel for another panel
     * in the parent window.  That is, determines if there are either no changes
     * or no changes the user wishes to save.
     * @return True if there are either no changes or no changes the user wishes
     * to save; False otherwise.
     */
    public boolean isOKToLeavePanel() {
        boolean changesFound = newChanges();
        if (changesFound && generalService.leaveWithoutSaving(parent)) {
            return true;
        } else if (!changesFound) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Resets the form to the state it was in when the form was opened. 
     *
     * @param evt The event that triggers the action.
     */
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        if(this.promptToReset()){
            setResource(currentResource);
        }
    }//GEN-LAST:event_resetButtonActionPerformed

    /**
     * Short function written to check if the name of a resource to be
     * edited is that of a default resource.
     * @param name Name of resource being edited
     * @return True if a default resource is being edited, false otherwise
     */
    private boolean nameCheck(String name) {
        if(name.equals(resourceNameField.getText())) {
            // Name is not being changed, the check is not needed.
            return false;
        }
        String cam = PropertyManager.getDefaultProperty("DEFAULT_WEATHER_CAMERA");
        String map = PropertyManager.getDefaultProperty("DEFAULT_WEATHER_MAP_LOOP");
        String sta = PropertyManager.getDefaultProperty("DEFAULT_WEATHER_STATION");
        if(name.equals(cam) || name.equals(sta) || name.equals(map)) {
            errorLabel.setForeground(Color.red);
            errorLabel.setText("You cannot edit the name of a default resource.");
            return true;
        }
        return false;
    }
    
    /**
     * Updates the
     * <code>Resource</code> in the database. Checks if the fields are valid,
     * then either adds to or updates the Resource database.
     *
     * @param evt The event that triggers the action.
     */
    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        //Save update state.
        boolean isNewResource = updateButton.getText().equals("Add");

        //Variable for success state.
        boolean success = false;   //Not yet saved.
        
        /**
         * Flag to not the possibility of an error preventing the retrieval of
         * video dimensions for a new video-making resource.  It is set to true
         * once there is an error.
         */
        boolean dimSetError = false;

        //Check to validate fields
        if (validateFields()) {
            //Don't change default names
            if (!nameCheck(currentResource.getResourceName())) {
                //Check URL verfication
                this.checkURLForSave();
                //Stop if URL is not verified
                if (urlVerified) {
                    //Build new resource
                    Resource newResource = buildResource();

                    //Abort save if user is not happy with new resource time 
                    //zone or does not want to put data with new time zone in 
                    //old storage folder
                    if (panelType != WeatherResourceType.WeatherMapLoop) {
                        if (isNewResource) {
                            if (!proceedWithFirstTimeZone()) {
                                return;
                            }
                        } else if (currentResource.getTimeZone() != newResource.getTimeZone()
                                && currentResource.getStorageFolderName()
                                .equals(newResource.getStorageFolderName())) {
                            if (!proceedWithNewTimeZoneInOldFolder()) {
                                return;
                            }
                        }
                    }

                    //Save work if new changes
                    if (newChanges()) {
                        /**
                         * If this is the initial save of a video-making
                         * resource, get its image dimensions from the Internet.
                         */

                        //This variable will hold the new dimension if one is 
                        //needed and remain null otherwise.
                        Dimension webDim = null;
                        if ((panelType == WeatherResourceType.WeatherCamera
                                || panelType == WeatherResourceType.WeatherMapLoop)
                                && isNewResource) {
                            webDim = getCurrentResourceDimension(newResource);

                            //Try to update resource.
                            if (webDim.height > 0 && webDim.width > 0) {
                                newResource.setImageWidth(webDim.width);
                                newResource.setImageHeight(webDim.height);
                            }
                        }

                        /**
                         * Set flag to state if a dimension retrieval error has
                         * taken place.
                         */
                        dimSetError = !(webDim == null || (webDim.width > 0
                                && webDim.height > 0));

                        //Update and retrieve new copy with resouce number
                        newResource = generalService.getDBMSSystem()
                                .getResourceManager()
                                .updateWeatherResource(newResource);

                        //Check success of operation.
                        success = newResource.getResourceNumber() != -1;

                        //Save attached weather station if approprite.
                        if (panelType == WeatherResourceType.WeatherCamera
                                && success) {
                            if (availableWeatherStations.getSelectedIndex()
                                    == 0) {
                                //"None" selected.
                                generalService.getDBMSSystem()
                                        .getResourceRelationManager()
                                        .removeResourceRelation(newResource);
                            } else {
                                for (Resource r : generalService
                                        .getWeatherStationResources()) {
                                    if (r.getResourceName()
                                            .equals(availableWeatherStations
                                                    .getSelectedItem())) {
                                        generalService.getDBMSSystem()
                                                .getResourceRelationManager()
                                                .setResourceRelation(newResource, r);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    //Set to the resource that was just saved if successful.
                    if (success) {
                        String newResourceName = resourceNameField.getText();
                        updateComboBox();
                        installedItemsList.setSelectedItem(newResourceName);
                    }

                    //Reset error label.
                    errorLabel.setText("");

                    //Show result.
                    if (isNewResource) {
                        this.showResourceAdded(success, dimSetError);
                    } else {
                        this.showResourceEdited(success);
                    }
                }
            }
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    /**
     * Validates the value in the field.  Tests the name in the field against the
     * list of current folders in the storage system. If the folder has a
     * resource associated with it, then it cannot be used again.  If the folder
     * name was used by an old resource, the user may be asked whether or not to
     * use it, depending on the value of the parameter.
     * @param promptForOldFolders Whether or not the user will be prompted when 
     * using an old, unused folder
     * @return Whether or not the folder is clear to use.
     */
    
    private boolean validateStorageFolder(boolean promptForOldFolders){
        if (storageSystemFolderList == null) {
            this.storageSystemFolderList = (StorageControlSystemImpl
                    .getStorageSystem()).retrieveFolderList();
            retrieveResourceBindings(storageSystemFolderList);
        }

        String field = storageFolderField.getText();

        if ((field.trim()).equals("") || field.trim().length() == 0) {
            errorLabel.setForeground(Color.red);
            errorLabel.setText("You must enter a folder name.");
            return false;
        }

        for (String s : storageSystemFolderList) {
            if (s.equals(field) && !field.equals(currentResource.getStorageFolderName())) {
                if (resourceFolderMap.get(s) == null) { //old, unused folder
                    if(promptForOldFolders){ //Should user be propted?
                        //Prompting user...
                        if(this.promptUseOldFolder()){
                            //User accepts old folder
                            errorLabel.setForeground(Color.green);
                            errorLabel.setText("The folder name is valid.");
                            return true;
                        } else {    //User rejects old folder.
                            errorLabel.setForeground(Color.red);
                            errorLabel.setText("You must enter a new folder name."
                                    + " or accept this one.");
                            return false;
                        }
                    } else { //Not prompting user
                        errorLabel.setForeground(Color.orange);
                        errorLabel.setText("This folder exists, but is currently "
                                + "not used.");
                        return false;
                    }
                } else {    //Folder currently in use
                    errorLabel.setForeground(Color.red);
                    errorLabel.setText("This folder name is already in use.");
                    return false;
                }
            }
        }

        if (!validateCharacters(field, "storage folder")) {
            return false;
        }
        errorLabel.setForeground(Color.green);
        errorLabel.setText("The folder name is valid.");
        return true;
    }
    
    
    /**
     * Function to validate folder names on every change of folder name
     * @param evt The event that triggers the action.
     */
    private void storageFolderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_storageFolderKeyReleased
        validateStorageFolder(false);
    }//GEN-LAST:event_storageFolderKeyReleased

    private void urlFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_urlFieldKeyReleased
        if (updateButton.getText().equals("Add")) {
            return;
        }
        if(!urlTextField.getText().equals(currentResource.getURL().toString())){
            urlVerified = false;
        }
    }//GEN-LAST:event_urlFieldKeyReleased

    /**
     * Button that links to Google Maps.
     * @param evt The event that triggers this action.
     */
    private void googleMapsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_googleMapsButtonActionPerformed
        String googleMaps="http://maps.google.com/maps?q=";
        BarebonesBrowser.openURL(googleMaps
                + latitudeField.getText() + "+,+" + longitudeField.getText(),
                this);
    }//GEN-LAST:event_googleMapsButtonActionPerformed

    /**
     * Explains to the user how to correctly find and enter the coordinates for
     * the resource.
     * @param evt The event that triggers this action.
     */
    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        String title = "Coordinate Help"; 
        String message = "Coordinates need to be as decimal degrees (ddd.ddddd).\n"
                + "To find the coordinates for the camera:\n"
                + "1. Navigate to the camera location.\n"
                + "2. Right click at that location and select \"What’s here?\"\n"
                + "3. The latitude/longitude coordinates will appear in the search bar at the top of the page.";
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }//GEN-LAST:event_helpButtonActionPerformed

    private void activeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activeCheckBoxActionPerformed
        //Inactive resources must be invisible.
        if (!activeCheckBox.isSelected()) {
            visibleCheckBox.setSelected(false);
        }
    }//GEN-LAST:event_activeCheckBoxActionPerformed

    private void visibleCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visibleCheckBoxActionPerformed
        //Visible resources must be active.
        if (visibleCheckBox.isSelected()) {
            activeCheckBox.setSelected(true);
        }
    }//GEN-LAST:event_visibleCheckBoxActionPerformed

    private void timeHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeHelpButtonActionPerformed
        String title = "Collection Span Selection Help";
        String message = "The time span must be given in the specified time\n"
                + "zone.  A 12 a.m. start time is the start of the day, while\n"
                + "a 12 a.m. end time is the end of the day, or the start\n"
                + "of the next day.";
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }//GEN-LAST:event_timeHelpButtonActionPerformed

    private void updateHourHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateHourHelpButtonActionPerformed
        String title = "Update Hour Help"; 
        String message = 
                "This is the hour of the day when the program will check\n"
                + "the Internet to see if the size of the images prduced by\n"
                + "the resource has changed.  If so, the video dimensions\n"
                + "will be changed to match.  The hour is given in the\n"
                + "resource's time zone.  Checking will only be done if the\n"
                + "resource is active.";
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(title);
        dialog.setIconImage(IconProperties.getTitleBarIconImage());
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }//GEN-LAST:event_updateHourHelpButtonActionPerformed

    private void webDimButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webDimButtonActionPerformed
        Dimension webDim = getCurrentResourceDimension(currentResource);

        //Check for error.
        if (webDim.width == 0 || webDim.height == 0) {
            String message = "Video dimensions could not be retrieved.";
            String title = "No Dimensions Retrieved";
            JOptionPane pane = new JOptionPane(message,
                JOptionPane.INFORMATION_MESSAGE);
            JDialog dialog = pane.createDialog(title);
            dialog.setIconImage(IconProperties.getTitleBarIconImage());
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
            return;
        }
        
        //Set fields.
        videoWidthField.setText("" + webDim.width);
        videoHeightField.setText("" + webDim.height);
    }//GEN-LAST:event_webDimButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox activeCheckBox;
    private javax.swing.JComboBox<String> availableWeatherStations;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.ButtonGroup collectionGroup;
    private javax.swing.JLabel collectionSpanLabel;
    private javax.swing.JPanel collectionSpanPanel;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel coordinatePanel;
    private javax.swing.JPanel dateInitiatedAndCheckBoxPanel;
    private javax.swing.JLabel dateInitiatedLabel;
    private javax.swing.JRadioButton daylightHoursOption;
    private javax.swing.JPanel donwloadFrequencyPanel;
    private javax.swing.JTextField downloadFrequencyField;
    private javax.swing.JLabel downloadFrequencyLabel;
    private javax.swing.JComboBox downloadFrequencyResolution;
    private javax.swing.JLabel endTimeLabel;
    private javax.swing.JComboBox endTimeList;
    private javax.swing.JPanel endTimePanel;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JComboBox<String> formatComboBox;
    private javax.swing.JLabel formatLabel;
    private javax.swing.JPanel formatPanel;
    private javax.swing.JRadioButton fullTimeOption;
    private javax.swing.JButton googleMapsButton;
    private javax.swing.JButton helpButton;
    private javax.swing.JComboBox<String> installedItemsList;
    private javax.swing.JTextField latitudeField;
    private javax.swing.JLabel latitudeLabel;
    private javax.swing.JTextField longitudeField;
    private javax.swing.JLabel longitudeLabel;
    private javax.swing.JButton resetButton;
    private javax.swing.JTextField resourceNameField;
    private javax.swing.JLabel resourceNameLabel;
    private javax.swing.JPanel resourceNamePanel;
    private javax.swing.JLabel resourceNumberLabel;
    private javax.swing.JPanel resourceNumberPanel;
    private javax.swing.JLabel resourceNumberValueLabel;
    private javax.swing.JRadioButton specifiedTimesOption;
    private javax.swing.JPanel specifiedTimesPanel;
    private javax.swing.JLabel startTimeLabel;
    private javax.swing.JComboBox startTimeList;
    private javax.swing.JPanel startTimePanel;
    private javax.swing.JLabel stationLinkLabel;
    private javax.swing.JPanel stationLinkPanel;
    private javax.swing.JButton storageFolderBrowseButton;
    private javax.swing.JTextField storageFolderField;
    private javax.swing.JLabel storageFolderLabel;
    private javax.swing.JPanel storageFolderPanel;
    private javax.swing.JButton testUrlButton;
    private javax.swing.JButton timeHelpButton;
    private javax.swing.JComboBox<String> timeZoneBox;
    private javax.swing.JLabel timeZoneLabel;
    private javax.swing.JButton timeZoneMapButton;
    private javax.swing.JPanel timeZonePanel;
    private javax.swing.JButton updateButton;
    private javax.swing.JButton updateHourHelpButton;
    private javax.swing.JLabel updateHourLabel;
    private javax.swing.JComboBox updateHourList;
    private javax.swing.JPanel updateHourPanel;
    private javax.swing.JPanel updateHourTimePanel;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JPanel urlPanel;
    private javax.swing.JTextField urlTextField;
    private javax.swing.JTextField videoHeightField;
    private javax.swing.JLabel videoHeightLabel;
    private javax.swing.JPanel videoSizePanel;
    private javax.swing.JTextField videoWidthField;
    private javax.swing.JLabel videoWidthLabel;
    private javax.swing.JCheckBox visibleCheckBox;
    private javax.swing.JButton webDimButton;
    // End of variables declaration//GEN-END:variables
}
