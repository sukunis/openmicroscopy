/*
 * org.openmicroscopy.shoola.agents.browser.BrowserAgent
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

/*------------------------------------------------------------------------------
 *
 * Written by:    Jeff Mellen <jeffm@alum.mit.edu>
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.browser;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.openmicroscopy.shoola.agents.annotator.events.AnnotateImage;
import org.openmicroscopy.shoola.agents.annotator.events.ImageAnnotated;
import org.openmicroscopy.shoola.agents.browser.heatmap.HeatMapManager;
import org.openmicroscopy.shoola.agents.browser.heatmap.HeatMapModel;
import org.openmicroscopy.ds.dto.SemanticType;
import org.openmicroscopy.ds.st.Category;
import org.openmicroscopy.ds.st.CategoryGroup;
import org.openmicroscopy.ds.st.Classification;
import org.openmicroscopy.ds.st.ImageAnnotation;
import org.openmicroscopy.ds.st.ImagePlate;
import org.openmicroscopy.ds.st.Pixels;
import org.openmicroscopy.is.ImageServerException;
import org.openmicroscopy.shoola.agents.browser.colormap.ColorMapManager;
import org.openmicroscopy.shoola.agents.browser.colormap.ColorMapModel;
import org.openmicroscopy.shoola.agents.browser.datamodel.*;
import org.openmicroscopy.shoola.agents.browser.events.AnnotateImageHandler;
import org.openmicroscopy.shoola.agents.browser.events.CategoryChangeHandler;
import org.openmicroscopy.shoola.agents.browser.events.ClassificationHandler;
import org.openmicroscopy.shoola.agents.browser.images.Thumbnail;
import org.openmicroscopy.shoola.agents.browser.images.ThumbnailDataModel;
import org.openmicroscopy.shoola.agents.browser.layout.NumColsLayoutMethod;
import org.openmicroscopy.shoola.agents.browser.layout.PlateLayoutMethod;
import org.openmicroscopy.shoola.agents.browser.ui.*;
import org.openmicroscopy.shoola.agents.browser.util.KillableThread;
import org.openmicroscopy.shoola.agents.classifier.events.CategoriesChanged;
import org.openmicroscopy.shoola.agents.classifier.events.ClassifyImage;
import org.openmicroscopy.shoola.agents.classifier.events.ClassifyImages;
import org.openmicroscopy.shoola.agents.classifier.events.DeclassifyImage;
import org.openmicroscopy.shoola.agents.classifier.events.DeclassifyImages;
import org.openmicroscopy.shoola.agents.classifier.events.ImagesClassified;
import org.openmicroscopy.shoola.agents.classifier.events.LoadCategories;
import org.openmicroscopy.shoola.agents.classifier.events.ReclassifyImage;
import org.openmicroscopy.shoola.agents.classifier.events.ReclassifyImages;
import org.openmicroscopy.shoola.agents.datamng.events.ViewImageInfo;
import org.openmicroscopy.shoola.agents.events.LoadDataset;
import org.openmicroscopy.shoola.env.Agent;
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.data.*;
import org.openmicroscopy.shoola.env.data.events.ServiceActivationRequest;
import org.openmicroscopy.shoola.env.data.model.DatasetData;
import org.openmicroscopy.shoola.env.data.model.ImageSummary;
import org.openmicroscopy.shoola.env.event.*;
import org.openmicroscopy.shoola.env.rnd.events.LoadImage;
import org.openmicroscopy.shoola.env.ui.UserNotifier;

/**
 * The agent class that connects the browser to the rest of the client
 * system, and receives events triggered by other parts of the client.
 * Subscribes and places events on the EventBus.  Also is (currently)
 * responsible for correctly initializing a browser window based on
 * information from the database.  This functionality can likely be
 * abstracted in a cleaner manner.
 * 
 * The BrowserAgent responds to the following events:
 * LoadDataset
 * ImageAnnotated
 * ImagesAnnotated
 * CategoriesChanged
 * 
 * The BrowserAgent places the following events on the queue:
 * LoadImage
 * ViewImageInfo
 * AnnotateImage
 * LoadCategories
 * ClassifyImage
 * ClassifyImages
 * ReclassifyImage
 * ReclassifyImages
 * 
 * @author Jeff Mellen, <a href="mailto:jeffm@alum.mit.edu">jeffm@alum.mit.edu</a><br><br>
 * <b>Internal Version:</b> $Revision$ $Date$
 * @version 2.2.1
 * @since OME2.2
 */
public class BrowserAgent implements Agent, AgentEventListener
{
    private Registry registry;
    private EventBus eventBus;
    private BrowserEnvironment env;
    private Map activeThreadMap;

    /**
     * Initialize the browser controller and register the OMEBrowerAgent with
     * the EventBus.
     */
    public BrowserAgent()
    {
        env = BrowserEnvironment.getInstance();
        env.setBrowserAgent(this);
        activeThreadMap = new IdentityHashMap();
    }
    
    /**
     * Activates the browser and initializes the environment.
     * Initializes the browser manager, heat map manager, and color
     * map manager for all browser windows.
     * 
     * @see org.openmicroscopy.shoola.env.Agent#activate()
     */
    public void activate()
    {
        // do nothing
    }
    
    /**
     * Checks if termination is possible (incomplete)
     * 
     * @see org.openmicroscopy.shoola.env.Agent#canTerminate()
     */
    public boolean canTerminate()
    {
        // do nothing yet; no browser state saved to DB or disk
        return true;
    }
    
    /**
     * Does termination stuff (incomplete). Browser does not currently
     * retain state, so nothing is necessary upon termination (yet).
     * 
     * @see org.openmicroscopy.shoola.env.Agent#terminate()
     */
    public void terminate()
    {
        BrowserManager manager = env.getBrowserManager();
        List browserList = manager.getAllBrowsers();
    }
    
    /**
     * Sets the context of the browser agent within the Shoola
     * environment.  Links the browser to the Registry, event bus,
     * icon manager, and looks up local configuration information
     * from the configuration XML files.  Finally, subscribes for
     * events in the EventBus and establishes a link with the
     * SemanticTypeService.
     * 
     * @param ctx The registry to retrieve container-level objects and
     *            local configuration information from.
     * @see org.openmicroscopy.shoola.env.Agent#setContext(org.openmicroscopy.shoola.env.config.Registry)
     */
    public void setContext(Registry ctx)
    {
        this.registry = ctx;
        this.eventBus = ctx.getEventBus();
        loadImageTypes(); // do this immediately to set up env
        env.setImageTypeList(loadImageTypes());
        
        env.setIconManager(IconManager.getInstance(ctx));
        env.setBrowserPreferences(new BrowserPreferences(ctx));
        
        eventBus.register(this,LoadDataset.class);
        eventBus.register(this,ImageAnnotated.class);
        eventBus.register(this,ImagesClassified.class);
        eventBus.register(this,CategoriesChanged.class);
        
        env.setBrowserManager(BrowserManagerSelector.getInstance(registry));
        env.setHeatMapManager(new HeatMapManager(registry));
        env.setColorMapManager(new ColorMapManager(registry));
    }
    
    /**
     * Instructs the agent to load the Dataset with the given ID into
     * a new browser window.
     * @param browserIndex The ID (primary key) of the dataset to load.
     * @return Whether or not the dataset was succesfully loaded.
     */
    public void loadDataset(int datasetID)
    {
        BrowserLoader loader = new BrowserLoader(registry,datasetID);
        loader.load();
        
        BrowserManager manager = env.getBrowserManager();
        BrowserWrapper bw;
        if((bw = manager.getBrowserForDataset(datasetID)) != null)
        {
            manager.setActiveBrowser(bw);
            return;
        }
        DataManagementService dms = registry.getDataManagementService();
        DatasetData dataset;
        
        final BrowserModel model = new BrowserModel();
        model.setLayoutMethod(new NumColsLayoutMethod(8));
        model.setDefaultLayoutMethod(new NumColsLayoutMethod(8));
        BrowserTopModel topModel = new BrowserTopModel();
        
        BPalette optionPalette = PaletteFactory.getOptionPalette(model,topModel);
        topModel.addPalette(UIConstants.OPTIONS_PALETTE_NAME,optionPalette);
        topModel.hidePalette(optionPalette);
        
        optionPalette.setOffset(0,0);
        BrowserView view = new BrowserView(model,topModel);
        final BrowserController controller = new BrowserController(model,topModel,view);
        controller.setStatusView(new StatusBar());

        final BrowserWrapper wrapper =
            env.getBrowserManager().addBrowser(controller);
        
        final int theDataset = datasetID;
        KillableThread retrieveThread = new KillableThread()
        {
            public void run()
            {
                addLoaderThread(controller,this);
                try
                {
                    DataManagementService dms =
                        registry.getDataManagementService();
                    DatasetData dataset = dms.retrieveDataset(theDataset);
                    model.setDataset(dataset);
                    if(!kill)
                    {
                        wrapper.setBrowserTitle("Image Browser: "+dataset.getName());
                        loadDataset(wrapper,dataset);
                    }
                    else
                    {
                        System.err.println("killed OK");
                    }
                }
                catch(DSAccessException dsae)
                {
                    UserNotifier notifier = registry.getUserNotifier();
                    notifier.notifyError("Data retrieval failure",
                    "Unable to retrieve dataset (id = " + theDataset + ")", dsae);
                    return;
                }
                catch(DSOutOfServiceException dsoe)
                {
                    // pop up new login window (eventually caught)
                    throw new RuntimeException(dsoe);
                }
                removeLoaderThread(controller,this);
            }
        };
        
        retrieveThread.start();
        writeStatusImmediately(controller.getStatusView(),
                               "Loading dataset from DB...");
            
    }
    
    // loads the information from the Dataset into a BrowserModel, and the
    // also is responsible for triggering the mechanism that loads all the
    // images.
    private boolean loadDataset(final BrowserWrapper wrapper,
                                DatasetData datasetModel)
    {
        // get that s**t out of here; call a proper parameter, man!
        if(datasetModel == null)
        {
            return false; // REEEEE-JECTED.
        }
        
        final BrowserController controller = wrapper.getController();
        final BrowserModel model = controller.getBrowserModel();
        final BrowserView view = controller.getView();
        final StatusBar status = controller.getStatusView();
        
        final DataManagementService dms =
            registry.getDataManagementService();
        
        final SemanticTypesService sts =
            registry.getSemanticTypesService();
            
        final PixelsService ps =
            registry.getPixelsService();
        
        // we're just going to assume that the DatasetData object does not
        // have the entire image list... might want to refactor this later.
        
        // always initialized as long as catch blocks return false
        List imageList;
        Map plateMap;
        
        Comparator idComparator = new Comparator()
        {
            public int compare(Object arg0, Object arg1)
            {
                if(arg0 == null)
                {
                    return -1;
                }
                
                if(arg1 == null)
                {
                    return 1;
                }
                
                if(!(arg0 instanceof ImageSummary) ||
                   !(arg1 instanceof ImageSummary))
                {
                    return 0;
                }
                
                ImageSummary is1 = (ImageSummary)arg0;
                ImageSummary is2 = (ImageSummary)arg1;
                
                if(is1.getID() < is2.getID())
                {
                    return -1;
                }
                else if(is1.getID() == is2.getID())
                {
                    return 0;
                }
                else
                {
                    return 1;
                }
            }
        };
        
        boolean plateMode = false;
        List plateList;
        List annotationList;
        Map annotationMap;
        Map classificationMap;
        PlateInfo plateInfo = new PlateInfo();
        
        try
        {
            // explicit interrupt check
            if(!activeThreadMap.containsKey(controller))
            {
                System.err.println("killed OK");
                return false;
            }
            // will this order by image ID?
            // should I explicitly order by another parameter?
            writeStatusImmediately(status,"Retrieving image records from DB...");
            imageList = dms.retrieveImages(datasetModel.getID());
            if(imageList == null)
            {
                UserNotifier un = registry.getUserNotifier();
                un.notifyError("Database Error","Invalid Dataset ID specified.");
                return false;
            }
            
            // empty list... processing unnecessary.
            if(imageList.size() == 0)
            {
                Runnable emptyTask = new Runnable()
                {
                    public void run()
                    {
                        status.setLeftText("Dataset contains no images.");
                    }
                };
                SwingUtilities.invokeLater(emptyTask);
                return true;
            }

            Collections.sort(imageList,idComparator);
            List idList = new ArrayList();
            
            // explicit interrupt check
            if(!activeThreadMap.containsKey(controller))
            {
                System.err.println("killed OK");
                return false;
            }
            // get plate information (if any) so that we can properly add
            // images
            writeStatusImmediately(status,"Retrieving plate information from DB...");
            for(int i=0;i<imageList.size();i++)
            {
                ImageSummary summary = (ImageSummary)imageList.get(i);
                idList.add(new Integer(summary.getID()));
            }
            
            plateList = sts.retrieveImageAttributes("ImagePlate",idList);
            
            // explicit interrupt check
            if(!activeThreadMap.containsKey(controller))
            {
                System.err.println("killed OK");
                return false;
            }
            writeStatusImmediately(status,"Retrieving annotation information from DB...");
            annotationList = sts.retrieveImageAttributes("ImageAnnotation",idList);
            annotationMap = new HashMap();
            
            if(annotationList != null)
            {
                for(Iterator iter = annotationList.iterator(); iter.hasNext();)
                {
                    ImageAnnotation ia = (ImageAnnotation)iter.next();
                    annotationMap.put(new Integer(ia.getImage().getID()),ia);
                }
            }
            writeStatusImmediately(status,"Loading in category types...");
            model.setCategoryTree(loadCategoryTree(datasetModel.getID()));
            
            writeStatusImmediately(status,"Retrieving classification information from DB...");
            classificationMap = new HashMap();
            List classificationList =
                    sts.retrieveImageClassifications(idList,datasetModel.getID());
            if(classificationList != null)
            {
                for(Iterator iter = classificationList.iterator(); iter.hasNext();)
                {
                    Classification c = (Classification)iter.next();
                    List cList =
                        (List)classificationMap.get(new Integer(c.getImage().getID()));
                    if(cList == null)
                    {
                        cList = new ArrayList();
                        cList.add(c);
                        classificationMap.put(new Integer(c.getImage().getID()),cList);
                    }
                    else
                    {
                        cList.add(c);
                    }
                }
            }
            ColorMapModel cmm = new ColorMapModel(model);
            ColorMapManager cManager = env.getColorMapManager();
            cManager.putColorMapModel(cmm);
            cManager.showModel(model.getDataset().getID());
            
            writeStatusImmediately(status,"Filling in relevant ST info from DB...");
            loadRelevantTypes(imageList,model,status);
            
            // going to assume that all image plates in dataset belong to
            // same plate (could be very wrong)
            if(plateList != null && plateList.size() > 0)
            {
                plateMode = true;
                String[] wellNames = new String[plateList.size()];
                for(int i=0;i<plateList.size();i++)
                {
                    ImagePlate plate = (ImagePlate)plateList.get(i);
                    wellNames[i] = plate.getWell();
                }
            
                plateInfo = PlateInfoParser.buildPlateInfo(wellNames);
            }
            
        }
        catch(DSOutOfServiceException dso)
        {
            UserNotifier un = registry.getUserNotifier();
            un.notifyError("Connection Error",dso.getMessage(),dso);
            return false;
        }
        catch(DSAccessException dsa)
        {
            UserNotifier un = registry.getUserNotifier();
            un.notifyError("Server Error",dsa.getMessage(),dsa);
            return false;
        }
        
        final Map imageMap = new HashMap();
        for(Iterator iter = imageList.iterator(); iter.hasNext();)
        {
            ImageSummary summary = (ImageSummary)iter.next();
            imageMap.put(new Integer(summary.getID()),summary);
        }
        
        status.processStarted(imageList.size());
        // see imageList initialization note above
        final List refList = Collections.unmodifiableList(imageList);
        final List refPlateList = Collections.unmodifiableList(plateList);
        final PlateInfo refInfo = plateInfo;
        final Map refAnnotations = annotationMap;
        final Map refClassifications = classificationMap;
        
        KillableThread plateLoader = new KillableThread()
        {
            public void run()
            {
                addLoaderThread(controller,this);
                final List thumbnails = new ArrayList();
                int count = 1;
                int total = refList.size();
                
                PlateLayoutMethod lm = new PlateLayoutMethod(refInfo.getNumRows(),
                                                             refInfo.getNumCols());
                model.setLayoutMethod(lm);
                model.setDefaultLayoutMethod(lm);
                
                CompletePlate plate = new CompletePlate();
                for(Iterator iter = refPlateList.iterator(); iter.hasNext();)
                {
                    ImagePlate ip = (ImagePlate)iter.next();
                    plate.put(ip.getWell(),new Integer(ip.getImage().getID()));
                }
                
                boolean wellSized = false;
                for(int i=0;i<refInfo.getNumRows();i++)
                {
                    for(int j=0;j<refInfo.getNumCols();j++)
                    {
                        // explicit break out
                        if(kill)
                        {
                            j=refInfo.getNumCols();
                            i=refInfo.getNumRows();
                            break;
                        }
                        String row = refInfo.getRowName(i);
                        String col = refInfo.getColumnName(j);
                        String well = row+col;
                        List sampleList = (List)plate.get(well);
                        
                        // allows for plate skips (in malformed datasets)
                        if(sampleList == null)
                        {
                            continue;
                        }
                        if(sampleList.size() == 1)
                        {
                            Integer intVal = (Integer)sampleList.get(0);
                            ImageSummary sum = (ImageSummary)imageMap.get(intVal);
                            try
                            {
                                Pixels pix = sum.getDefaultPixels().getPixels();
                                Image image = ps.getThumbnail(pix);
                                if(!wellSized)
                                {
                                    lm.setWellWidth(image.getWidth(null));
                                    lm.setWellHeight(image.getHeight(null));
                                    wellSized = true;
                                }
                                ThumbnailDataModel tdm = new ThumbnailDataModel(sum);
                                tdm.setValue(UIConstants.WELL_KEY_STRING,well);
                                tdm.getAttributeMap().putAttribute(pix);
                                
                                ImageAnnotation annotation =
                                    (ImageAnnotation)refAnnotations.get(new Integer(sum.getID()));        
                                if(annotation != null)
                                {
                                    tdm.getAttributeMap().putAttribute(annotation);
                                }
                                
                                List classificationList =
                                    (List)refClassifications.get(new Integer(sum.getID()));
                                if(classificationList != null)
                                {
                                    for(Iterator iter = classificationList.iterator(); iter.hasNext();)
                                    {
                                        Classification c = (Classification)iter.next();
                                        tdm.getAttributeMap().putAttribute(c);
                                    }
                                }
                                
                                final Thumbnail t = new Thumbnail(image,tdm);
                                lm.setIndex(t,i,j);
                                
                                final int theCount = count;
                                final int theTotal = total;
                                Runnable addTask = new Runnable()
                                {
                                    public void run()
                                    {
                                        thumbnails.add(t);
                                        String message =
                                            ProgressMessageFormatter.format("Loaded image %n of %t...",
                                                                            theCount,theTotal);
                                        status.processAdvanced(message);
                                    }
                                };
                                SwingUtilities.invokeLater(addTask);
                                count++;
                            }
                            catch(ImageServerException ise)
                            {
                                UserNotifier un = registry.getUserNotifier();
                                un.notifyError("ImageServer Error",ise.getMessage(),ise);
                                status.processFailed("Error loading images.");
                                return;
                            }
                        }
                        else
                        {
                            Image[] images = new Image[sampleList.size()];
                            ThumbnailDataModel[] models =
                                new ThumbnailDataModel[sampleList.size()];
                            for(int k=0;k<sampleList.size();k++)
                            {
                                Integer intVal = (Integer)sampleList.get(k);
                                ImageSummary sum = (ImageSummary)imageMap.get(intVal);
                                try
                                {
                                    Pixels pix = sum.getDefaultPixels().getPixels();
                                    Image image = ps.getThumbnail(pix);
                                    ThumbnailDataModel tdm = new ThumbnailDataModel(sum);
                                    tdm.setValue(UIConstants.WELL_KEY_STRING,well);
                                    tdm.getAttributeMap().putAttribute(pix);
                                    ImageAnnotation annotation =
                                        (ImageAnnotation)refAnnotations.get(new Integer(sum.getID()));
                                        
                                    if(annotation != null)
                                    {
                                        tdm.getAttributeMap().putAttribute(annotation);
                                    }
                                    
                                    List classificationList =
                                        (List)refClassifications.get(new Integer(sum.getID()));
                                    if(classificationList != null)
                                    {
                                        for(Iterator iter = classificationList.iterator(); iter.hasNext();)
                                        {
                                            Classification c = (Classification)iter.next();
                                            tdm.getAttributeMap().putAttribute(c);
                                        }
                                    }
                                    images[k] = image;
                                    models[k] = tdm;
                                    count++;
                                }
                                catch(ImageServerException ise)
                                {
                                    UserNotifier un = registry.getUserNotifier();
                                    un.notifyError("ImageServer Error",ise.getMessage(),ise);
                                    status.processFailed("Error loading images.");
                                    return;
                                }
                            }
                            
                            final Thumbnail t = new Thumbnail(images,models);
                            lm.setIndex(t,i,j);
                            final int theCount = count;
                            final int theTotal = total;
                            
                            Runnable addTask = new Runnable()
                            {
                                public void run()
                                {
                                    String message =
                                        ProgressMessageFormatter.format("Loaded image %n of %t...",
                                                                        theCount,theTotal);
                                    status.processAdvanced(message);
                                    thumbnails.add(t);
                                }
                            };
                            SwingUtilities.invokeLater(addTask);
                        }
                    }
                }
                
                if(!kill)
                {
                    Runnable finalTask = new Runnable()
                    {
                        public void run()
                        {
                            Thumbnail[] ts = new Thumbnail[thumbnails.size()];
                            thumbnails.toArray(ts);
                            model.addThumbnails(ts);
                            status.processSucceeded("All images loaded.");
                        }
                    };
                    SwingUtilities.invokeLater(finalTask);
                    return;
                }
                else
                {
                    System.err.println("killed OK");
                }
                removeLoaderThread(controller,this);
            }
        };
        
        KillableThread loader = new KillableThread()
        {
            public void run()
            {
                addLoaderThread(controller,this);
                int count = 1;
                int total = refList.size();
                
                for(Iterator iter = refList.iterator(); (iter.hasNext() && !kill);)
                {
                    ImageSummary summary = (ImageSummary)iter.next();
                    
                    try
                    {
                        Pixels pix = summary.getDefaultPixels().getPixels();
                        Image image = ps.getThumbnail(pix);
                        ThumbnailDataModel tdm = new ThumbnailDataModel(summary);
                        tdm.getAttributeMap().putAttribute(pix);
                        ImageAnnotation annotation =
                            (ImageAnnotation)refAnnotations.get(new Integer(summary.getID()));
                            
                        if(annotation != null)
                        {
                            tdm.getAttributeMap().putAttribute(annotation);
                        }
                        List classificationList =
                            (List)refClassifications.get(new Integer(summary.getID()));
                        if(classificationList != null)
                        {
                            for(Iterator iter2 = classificationList.iterator(); iter2.hasNext();)
                            {
                                Classification c = (Classification)iter2.next();
                                tdm.getAttributeMap().putAttribute(c);
                            }
                        }
                        final Thumbnail t = new Thumbnail(image,tdm);
                        
                        final int theCount = count;
                        final int theTotal = total;
                        Runnable addTask = new Runnable()
                        {
                            public void run()
                            {
                                model.addThumbnail(t);
                                String message =
                                    ProgressMessageFormatter.format("Loaded image %n of %t...",
                                                            theCount,theTotal);
                                status.processAdvanced(message);
                            }
                        };
                        SwingUtilities.invokeLater(addTask);
                        count++;
                    }
                    catch(ImageServerException ise)
                    {
                        UserNotifier un = registry.getUserNotifier();
                        un.notifyError("ImageServer Error",ise.getMessage(),ise);
                        status.processFailed("Error loading images.");
                        return;
                    }
                }
                
                if(!kill)
                {
                    Runnable finalTask = new Runnable()
                    {
                        public void run()
                        {
                            status.processSucceeded("All images loaded.");
                        }
                    };
                    SwingUtilities.invokeLater(finalTask);
                    return;
                }
                removeLoaderThread(controller,this);
            }
        };
        
        // explicit interrupt check
        if(!activeThreadMap.containsKey(controller))
        {
            System.err.println("killed OK");
            return false;
        }
        
        if(plateMode)
        {
            plateLoader.start();
        }
        else
        {
            loader.start();
        }
        
        return true;
    }
    
    // display content information immediately.
    private void writeStatusImmediately(final StatusBar status,
                                        final String message)
    {
        Runnable writeTask = new Runnable()
        {
            public void run()
            {
                status.setLeftText(message);
            }
        };
        SwingUtilities.invokeLater(writeTask);
    }

    /**
     * Instructs the agent to load the Dataset with the given ID into the
     * specified browser.
     * 
     * @param browserIndex The index of the browser window to load.
     * @param datasetID The ID of the dataset to load.
     * @return true If the load was successful, false if not.
     */
    public void loadDataset(int browserIndex, int datasetID)
    {
        final int theDataset = datasetID;
        final int theIndex = browserIndex;
        BrowserManager manager = env.getBrowserManager();
        final BrowserWrapper browser = manager.getBrowser(browserIndex);
        
        BrowserController controller = browser.getController();
        
        final BrowserModel model = controller.getBrowserModel();
        
        Thread retrieveThread = new Thread()
        {
            public void run()
            {
                try
                {
                    DataManagementService dms =
                        registry.getDataManagementService();
                    DatasetData dataset = dms.retrieveDataset(theDataset);
                    model.setDataset(dataset);
                    browser.setBrowserTitle("Image Browser: "+dataset.getName());
                    loadDataset(browser,dataset);
                }
                catch(DSAccessException dsae)
                {
                    UserNotifier notifier = registry.getUserNotifier();
                    notifier.notifyError("Data retrieval failure",
                    "Unable to retrieve dataset (id = " + theDataset + ")", dsae);
                    return;
                }
                catch(DSOutOfServiceException dsoe)
                {
                    // pop up new login window (eventually caught)
                    throw new RuntimeException(dsoe);
                }
            }
        };
    }
    
    /**
     * Fills the model with a list of pertinent image-granular attributes.
     * @param model The model to load.
     */
    private void loadRelevantTypes(List imageList, BrowserModel targetModel,
                                   StatusBar status)
    {
        if(imageList == null || targetModel == null) return;
        List imageTypeList = env.getImageTypeList();
        List relevantTypes = new ArrayList();
        SemanticTypesService sts = registry.getSemanticTypesService();
        
        List integerList = new ArrayList();
        
        for(Iterator iter = imageList.iterator(); iter.hasNext();)
        {
            ImageSummary is = (ImageSummary)iter.next();
            integerList.add(new Integer(is.getID()));
        }
        
        for(int i=0;i<imageTypeList.size();i++)
        {
            SemanticType st = (SemanticType)imageTypeList.get(i);
            try
            {
                writeStatusImmediately(status,"Counting "+st.getName()+
                                       " attributes from DB ("+
                                       (i+1)+"/"+imageTypeList.size()+")");
                int count = sts.countImageAttributes(st,integerList);
                if(count > 0)
                {
                    relevantTypes.add(st);
                } 
            }
            catch(DSAccessException dsa)
            {
                UserNotifier un = registry.getUserNotifier();
                un.notifyError("Server Error","Could not count attributes",dsa);
            }
            catch(DSOutOfServiceException dso)
            {
                UserNotifier un = registry.getUserNotifier();
                un.notifyError("Communication Error","Could not retrieve count",dso);
            }
        }
        
        SemanticType[] types = new SemanticType[relevantTypes.size()];
        relevantTypes.toArray(types);
        targetModel.setRelevantTypes(types);
        writeStatusImmediately(status,"Filling in analyzed semantic types...");
        HeatMapModel hmm = new HeatMapModel(targetModel);
        HeatMapManager manager = env.getHeatMapManager();
        manager.putHeatMapModel(hmm);
        manager.showModel(targetModel.getDataset().getID());
    }
    
    // keeps track of the time-consuming loader threads.
    private void addLoaderThread(BrowserController loader,
                                 KillableThread thread)
    {
        if(activeThreadMap.containsKey(loader))
        {
            List list = (List)activeThreadMap.get(loader);
            list.add(thread);
        }
        else
        {
            List list = new ArrayList();
            list.add(thread);
            activeThreadMap.put(loader,list);
        }
    }
    
    // remove a loader thread from the list of active threads.
    private void removeLoaderThread(BrowserController loader,
                                    KillableThread thread)
    {
        if(activeThreadMap.containsKey(loader))
        {
            List list = (List)activeThreadMap.get(loader);
            list.remove(thread);
            if(list.size() == 0)
            {
                activeThreadMap.remove(loader);
            }
        }
    }
    
    /**
     * Indicates browser shutdown; interrupt any threads associated with
     * this browser.
     * @param loader The browser to cancel loading.
     */
    public void interruptThread(BrowserController loader)
    {
        if(activeThreadMap.containsKey(loader))
        {
            List list = (List)activeThreadMap.get(loader);
            for(Iterator iter = list.iterator(); iter.hasNext();)
            {
                KillableThread kt = (KillableThread)iter.next();
                kt.kill();
            }
            activeThreadMap.remove(loader);
        }
    }
    
    /**
     * Instruct the BrowserAgent to fire a LoadImage event, to show
     * the current image and pixels represented in the thumbnail.
     * @param t The thumbnail of the image to load in the viewer.
     */
    public void loadImage(Thumbnail t)
    {
        ThumbnailDataModel tdm = t.getModel(); // gets current model
        int imageID = tdm.getID();
        Pixels pixels = (Pixels)tdm.getAttributeMap().getAttribute("Pixels");
        int pixelsID = pixels.getID();
        
        loadImage(imageID, pixelsID, tdm.getName());
    }

    /**
     * Instruct the BrowserAgent to fire a LoadImage event, to be handled
     * by another part of the client.
     * 
     * @param imageID The ID of the image to load (in a viewer, for example)
     */
    public void loadImage(int imageID, int pixelsID, String ImgName)
    {
        LoadImage imageEvent = new LoadImage(imageID, pixelsID, ImgName);
        EventBus eventBus = registry.getEventBus();
        eventBus.post(imageEvent);
    }
    
    /**
     * Use the DM to show image info about a particular thumbnail.
     * @param t The thumbnail to query.
     */
    public void showImageInfo(Thumbnail t)
    {
        if(t == null) return;
        ThumbnailDataModel tdm = t.getModel();
        ImageSummary is = tdm.getImageInformation();
        showImageInfo(is);
    }
    
    /**
     * Use the DM to visualize the specified ImageSummary data object,
     * located (likely) inside a Thumbnail's ThumbnailDataModel.
     * @param is The image summary to show.
     */
    public void showImageInfo(ImageSummary is)
    {
        if(is == null) return;
        ViewImageInfo imageInfoEvent = new ViewImageInfo(is);
        EventBus eventBus = registry.getEventBus();
        eventBus.post(imageInfoEvent);
    }
    
    /**
     * Use the Annotator to annotate the image currently selected in the
     * specified thumbnail.
     * @param t The thumbnail with the image to annotate.
     */
    public void annotateImage(Thumbnail t)
    {
        if(t == null) return;
        ThumbnailDataModel tdm = t.getModel();
        ImageSummary summary = tdm.getImageInformation();
        annotateImage(summary,null); // default popup location
    }
    
    /**
     * Use the Annotator to annotate the image currently selected in the
     * specified thumbnail.
     * @param t The thumbnail with the image to annotate.
     * @param popupLocation The location to pop up the annotator window,
     *                      in screen coordinates.
     */
    public void annotateImage(Thumbnail t, Point popupLocation)
    {
        if(t == null) return;
        ThumbnailDataModel tdm = t.getModel();
        ImageSummary summary = tdm.getImageInformation();
        annotateImage(summary,popupLocation);
    }
    
    /**
     * Use the Annotator to annotate the image with the specified ID.
     * @param imageID The ID of the image to annotate.
     * @param popupLocation (optional) The location to pop up the annotator
     *                                 window, in screen coordinates.
     */
    public void annotateImage(ImageSummary imageInfo, Point popupLocation)
    {
        int[] pixelsID = imageInfo.getPixelsIDs();
        AnnotateImage event = new AnnotateImage(imageInfo.getID(),
                                                imageInfo.getName(), 
                                                pixelsID[0]);
        if(popupLocation != null)
        {
            event.setSpecifiedLocation(popupLocation);
        }
        
        // makes sure correct response occurs
        event.setCompletionHandler(new AnnotateImageHandler());
        EventBus eventBus = registry.getEventBus();
        eventBus.post(event);
    }

    /**
     * Instruct the BrowserAgent to fire a LoadImages event, to be handled
     * by another part of the client.
     * 
     * @param IDs The IDs of the image to load (in a viewer, for example)
     */
    public void loadImages(int[] IDs)
    {
        if (IDs == null || IDs.length == 0)
        {
            return;
        }
        // TODO: fill in loadImages(int[])
    }
    
    /**
     * Instruct the BrowserAgent to fire a LoadCategories event, to be
     * processed by another part of the client.
     * 
     * @param datasetID The ID of the dataset to trigger.
     * @param displayName The name of the category edit box to display.
     */
    public void loadCategories(int datasetID, String displayName)
    {
        LoadCategories event = new LoadCategories(datasetID,displayName);
        event.setCompletionHandler(new CategoryChangeHandler());
        EventBus eventBus = registry.getEventBus();
        eventBus.post(event);
    }
    
    /**
     * Gets a thumbnail of a different size (same settings)
     * @param pix The pixels that are the base of the thumbnail.
     * @param width The width of the image with the default thumb settings
     *              to retrieve.
     * @param height The height of the image with the default thumb settings
     *               to retrieve.
     * @return A new composite.
     */
    public Image getResizedThumbnail(Pixels pix, int width, int height)
    {
        if(pix == null)
        {
            return null;
        }
        
        PixelsService ps = registry.getPixelsService();
        try
        {
            return ps.getThumbnail(pix,width,height);
        }
        catch(ImageServerException ise)
        {
            // don't do user notification, make this more subtle
            System.err.println("could not load composite thumbnail");
            return null;
        }
    }
    
    /**
     * Loads the semantic type with the given name.
     * @param typeName The name fo the type to retrieve.
     * @return The SemanticType corresponding to the specified name.
     */
    public SemanticType loadTypeInformation(String typeName)
    {
        SemanticTypesService sts = registry.getSemanticTypesService();
        try
        {
            return sts.retrieveSemanticType(typeName);
        }
        catch(DSOutOfServiceException dso)
        {
            UserNotifier un = registry.getUserNotifier();
            un.notifyError("Connection Error",dso.getMessage(),dso);
        }
        catch(DSAccessException dsa)
        {
            UserNotifier un = registry.getUserNotifier();
            un.notifyError("Server Error",dsa.getMessage(),dsa);
        }
        return null; // fallback case
    }
    
    /**
     * Returns a category tree for a dataset with the particular ID.
     * @param datasetID The ID of the dataset to load categories for.
     * @return A hierarchy of the phenotype groups and categories for that
     *         dataset (corresponding to CategoryGroup and Category in the DB)
     */
    public CategoryTree loadCategoryTree(int datasetID)
    {
        SemanticTypesService sts = registry.getSemanticTypesService();
        List categoryList;
        List categoryGroupList;
        try
        {
            categoryGroupList =
                sts.retrieveDatasetAttributes("CategoryGroup",datasetID);
            categoryList =
                sts.retrieveDatasetAttributes("Category",datasetID);
                
            CategoryTree tree = new CategoryTree();
            for(Iterator iter = categoryGroupList.iterator(); iter.hasNext();)
            {
                CategoryGroup cg = (CategoryGroup)iter.next();
                tree.addCategoryGroup(cg);
            }
            for(Iterator iter = categoryList.iterator(); iter.hasNext();)
            {
                Category c = (Category)iter.next();
                tree.addCategory(c.getCategoryGroup(),c);
            }
            return tree;
        }
        catch(DSAccessException dsae)
        {
            UserNotifier un = registry.getUserNotifier();
            un.notifyError("Phenotype loading error",
                           "Unable to load categories",dsae);
        }
        catch(DSOutOfServiceException dsoe)
        {
            ServiceActivationRequest request = new ServiceActivationRequest(
                                        ServiceActivationRequest.DATA_SERVICES);
            registry.getEventBus().post(request);
        }
        return null; // TODO change
    }
    
    /**
     * Adds a single (new) image classification to the DB, and fires the
     * appropriate ClassifyImage event.
     * @param imageID The ID of the image to newly classify.
     * @param category Which category to classify the image under.
     */
    public void classifyImage(int imageID, Category category)
    {
        if(category == null) return;
        ClassifyImage classifyEvent = new ClassifyImage(imageID,category);
        classifyEvent.setCompletionHandler(new ClassificationHandler());
        eventBus.post(classifyEvent);
    }
    
    /**
     * Adds new classifications to the DB, mapping the specified images
     * to the specified category.  Fires a ClassifyImages event.
     * @param imageIDs The IDs of the images to newly classify.
     * @param category Which category to classify the image as.
     */
    public void classifyImages(int[] imageIDs, Category category)
    {
        if(imageIDs == null || imageIDs.length == 0 || category == null)
        {
            return;
        }
        
        ClassifyImages classifyEvent = new ClassifyImages(imageIDs,category);
        classifyEvent.setCompletionHandler(new ClassificationHandler());
        eventBus.post(classifyEvent);
    }
    
    /**
     * Changes the stored classification of a single image.
     * @param modifiedClassification The new classification.
     */
    public void reclassifyImage(Classification modifiedClassification)
    {
        if(modifiedClassification == null) return;
        ReclassifyImage classifyEvent =
            new ReclassifyImage(modifiedClassification);
        classifyEvent.setCompletionHandler(new ClassificationHandler());
        eventBus.post(classifyEvent);
    }
    
    /**
     * Changes the stored classification of multiple images.
     * @param modified The new classifications.
     */
    public void reclassifyImages(Classification[] modified)
    {
        if(modified == null || modified.length == 0) return;
        List classificationList = Arrays.asList(modified);
        ReclassifyImages classifyEvent =
            new ReclassifyImages(classificationList);
        classifyEvent.setCompletionHandler(new ClassificationHandler());
        eventBus.post(classifyEvent);
    }
    
    /**
     * Invalidates the classification (over a certain category) of a single image.
     * (BUG 117 FIX)
     * @param invalidClassification The classification to "erase."
     */
    public void declassifyImage(Classification invalidClassification)
    {
        if(invalidClassification == null) return;
        DeclassifyImage classifyEvent =
            new DeclassifyImage(invalidClassification);
        classifyEvent.setCompletionHandler(new ClassificationHandler());
        eventBus.post(classifyEvent);
    }
    
    /**
     * Invalidates the specified classifications, usually of multiple images.
     * (BUG 117 FIX)
     * @param invalid The classifications to "erase."
     */
    public void declassifyImages(Classification[] invalid)
    {
        if(invalid == null || invalid.length == 0) return;
        List classificationList = Arrays.asList(invalid);
        DeclassifyImages classifyEvent =
            new DeclassifyImages(classificationList);
        classifyEvent.setCompletionHandler(new ClassificationHandler());
        eventBus.post(classifyEvent);
    }
    
    /**
     * TODO: maybe hide this later in favor of doing something that ensures
     *       browser will be notified of changes?
     * @return The STS behind this agent.
     */
    public SemanticTypesService getSemanticTypesService()
    {
        return registry.getSemanticTypesService();
    }
    
    //  loads the image types from the database.
    private List loadImageTypes()
    {
        // test code to check for image STs
        List imageTypeList = new ArrayList();
        SemanticTypesService sts = registry.getSemanticTypesService();
        try
        {
            List typeList = sts.getAvailableImageTypes();
            for(Iterator iter = typeList.iterator(); iter.hasNext();)
            {
                SemanticType st = (SemanticType)iter.next();
                imageTypeList.add(st);
            }
        }
        catch(DSOutOfServiceException dso)
        {
            dso.printStackTrace();
            UserNotifier un = registry.getUserNotifier();
            un.notifyError("Connection Error",dso.getMessage(),dso);
        }
        catch(DSAccessException dsa)
        {
            dsa.printStackTrace();
            UserNotifier un = registry.getUserNotifier();
            un.notifyError("Server Error",dsa.getMessage(),dsa);
        }
        return imageTypeList;
    }
    
    /**
     * Responds to an event on the event bus.
     * 
     * @see org.openmicroscopy.shoola.env.event.AgentEventListener#eventFired(org.openmicroscopy.shoola.env.event.AgentEvent)
     */
    public void eventFired(AgentEvent e)
    {
        if(e instanceof LoadDataset)
        {
            LoadDataset event = (LoadDataset)e;
            loadDataset(event.getDatasetID());
        }
        else if(e instanceof ImageAnnotated)
        {
            ImageAnnotated event = (ImageAnnotated)e;
            event.complete();
        }
        else if(e instanceof ImagesClassified)
        {
            ImagesClassified event = (ImagesClassified)e;
            event.complete();
        }
        else if(e instanceof CategoriesChanged)
        {
            CategoriesChanged event = (CategoriesChanged)e;
            event.complete();
        }
    }
}
