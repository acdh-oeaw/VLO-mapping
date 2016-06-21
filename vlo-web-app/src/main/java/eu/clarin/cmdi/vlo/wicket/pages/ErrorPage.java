/*
 * Copyright (C) 2016 CLARIN
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
package eu.clarin.cmdi.vlo.wicket.pages;

import eu.clarin.cmdi.vlo.VloWebAppParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ErrorPage extends VloBasePage {

    public final static String PAGE_PARAMETER_RESPONSE_CODE = "code";

    private final int responseCode;

    public ErrorPage(PageParameters parameters) {
        super(parameters);
        this.responseCode = parameters.get(PAGE_PARAMETER_RESPONSE_CODE).toInt(500);

        final PageParameters queryParams = new PageParameters(parameters).remove(PAGE_PARAMETER_RESPONSE_CODE);
        final boolean hasQuery = !queryParams.get(VloWebAppParameters.QUERY).isEmpty() || !queryParams.get(VloWebAppParameters.FILTER_QUERY).isEmpty();
        add(new BookmarkablePageLink("searchPage", FacetedSearchPage.class, queryParams)
                .add(new Label("label", hasQuery ? "Return to query" : "Go to the search page"))
        );
    }

    @Override
    protected void configureResponse(WebResponse response) {
        super.configureResponse(response);
        response.setStatus(responseCode);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        //error page should not be indexed
        response.render(MetaDataHeaderItem.forMetaTag("robots", "noindex"));
    }

    public static void triggerErrorPage(int statusCode) {
        triggerErrorPage(statusCode, null);
    }

    public static void triggerErrorPage(int statusCode, PageParameters pageParameters) {
        final PageParameters params;
        if (pageParameters == null) {
            params = new PageParameters();
        } else {
            params = new PageParameters(pageParameters);
        }

        throw new RestartResponseException(ErrorPage.class, params.add(PAGE_PARAMETER_RESPONSE_CODE, statusCode));
    }

}
