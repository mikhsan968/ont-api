/*
 * This file is part of the ONT API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2017, Avicomp Services, AO
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package ru.avicomp.ontapi.jena;

import org.apache.jena.graph.Graph;
import org.apache.jena.mem.GraphMem;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.system.JenaSystem;

import ru.avicomp.ontapi.jena.impl.OntGraphModelImpl;
import ru.avicomp.ontapi.jena.impl.configuration.OntModelConfig;
import ru.avicomp.ontapi.jena.impl.configuration.OntPersonality;
import ru.avicomp.ontapi.jena.model.OntGraphModel;

/**
 * Access point to {@link OntGraphModel}, common {@link Model} and {@link Graph}.
 * The our analogue of {@link org.apache.jena.rdf.model.ModelFactory}.
 * Please use it to avoid ExceptionInInitializerError during JenaSystem.init()
 * <p>
 * Created by szuev on 14.02.2017.
 */
public class OntModelFactory {

    static {
        init();
    }

    /**
     * force init before any ont-model initializations here due to bug(?) in jena-arq-3.2.0 (upgrade 3.1.0 -> 3.2.0)
     * otherwise java.lang.ExceptionInInitializerError may occur.
     * to test (on 3.2.0) just run "new org.apache.jena.rdf.model.impl.ModelCom(null)" without (before) any JenaSystem.init();
     */
    public static void init() {
        JenaSystem.init();
    }

    public static Graph createDefaultGraph() {
        return new GraphMem();
    }

    public static Model createDefaultModel() {
        return new ModelCom(createDefaultGraph());
    }

    public static OntGraphModel createModel() {
        return createModel(createDefaultGraph());
    }

    public static OntGraphModel createModel(Graph graph) {
        return createModel(graph, OntModelConfig.getPersonality());
    }

    public static OntGraphModel createModel(Graph graph, OntPersonality personality) {
        return new OntGraphModelImpl(graph, personality);
    }

    /**
     * Returns ont-personality.
     * It is here, since i'm not sure it is good to be placed in {@link OntGraphModel}: personality is an internal object originally.
     *
     * @param model {@link OntGraphModel}
     * @return {@link OntPersonality}
     * @throws OntJenaException   checking for null
     * @throws ClassCastException in case it's not default implementation.
     */
    public static OntPersonality getPersonality(OntGraphModel model) {
        return ((OntGraphModelImpl) OntJenaException.notNull(model, "Null model")).getPersonality();
    }
}
