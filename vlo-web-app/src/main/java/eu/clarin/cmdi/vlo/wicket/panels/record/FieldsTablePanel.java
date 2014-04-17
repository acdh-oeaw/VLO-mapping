/*
 * Copyright (C) 2014 CLARIN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.clarin.cmdi.vlo.wicket.panels.record;

import eu.clarin.cmdi.vlo.config.VloConfig;
import eu.clarin.cmdi.vlo.pojo.DocumentField;
import eu.clarin.cmdi.vlo.pojo.FacetSelection;
import eu.clarin.cmdi.vlo.pojo.QueryFacetsSelection;
import eu.clarin.cmdi.vlo.service.PageParametersConverter;
import eu.clarin.cmdi.vlo.wicket.model.SolrFieldNameModel;
import eu.clarin.cmdi.vlo.wicket.pages.FacetedSearchPage;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *
 * @author twagoo
 */
public class FieldsTablePanel extends Panel {

    @SpringBean
    private VloConfig vloConfig;
    @SpringBean(name = "queryParametersConverter")
    private PageParametersConverter<QueryFacetsSelection> paramsConverter;

    public FieldsTablePanel(String id, IDataProvider<DocumentField> fieldProvider) {
        super(id);
        add(new DataView<DocumentField>("documentField", fieldProvider) {

            @Override
            protected void populateItem(final Item<DocumentField> item) {
                final IModel<DocumentField> fieldModel = item.getModel();
                final PropertyModel<String> fieldNameModel = new PropertyModel<String>(fieldModel, "fieldName");
                final SolrFieldNameModel friendlyFieldNameModel = new SolrFieldNameModel(fieldNameModel);
                item.add(new Label("fieldName", friendlyFieldNameModel));
                final PropertyModel<List> valuesModel = new PropertyModel<List>(fieldModel, "values");
                item.add(new ListView("values", valuesModel) {

                    @Override
                    protected void populateItem(final ListItem fieldValueItem) {
                        fieldValueItem.add(new Label("value", fieldValueItem.getModel()));
                        fieldValueItem.add(createFacetSelectLink("facetSelect", fieldNameModel, fieldValueItem.getModel()));
                    }
                });

                // if field has multiple values, set 'multiple' class on markup element
                item.add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (valuesModel.getObject().size() > 1) {
                            return "multiplevalues";
                        } else {
                            return null;
                        }
                    }
                }));
            }
        });
    }

    private Link createFacetSelectLink(String id, final IModel<String> facetNameModel, final IModel valueModel) {
        return new Link(id) {

            @Override
            public void onClick() {
                final FacetSelection facetSelection = new FacetSelection(Collections.singleton(valueModel.getObject().toString()));
                final QueryFacetsSelection selection = new QueryFacetsSelection(Collections.singletonMap(facetNameModel.getObject(), facetSelection));
                setResponsePage(FacetedSearchPage.class, paramsConverter.toParameters(selection));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                // only show for facet fields
                setVisible(isShowFacetSelectLinks()
                        && vloConfig.getFacetFields().contains(facetNameModel.getObject()));
            }

        };
    }

    protected boolean isShowFacetSelectLinks() {
        return true;
    }

}
