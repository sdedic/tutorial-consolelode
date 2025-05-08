/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package one.dedic.consolelode;

import com.googlecode.lanterna.input.KeyStroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author sdedic
 */
public class DelegatingController extends Controller {
    private List<Controller> delegates = new ArrayList<>();
    
    public void addDelegate(Controller delegate) {
        delegates.add(delegate);

        if (screen != null) {
            setupDelegate(delegate);
        }
    }
    
    public void removeDelegate(Controller delegate) {
        delegates.remove(delegate);
    }
    
    protected Stream<Controller> delegates() {
        return delegates.stream();
    }

    @Override
    protected NextState handle(KeyStroke ks) {
        return delegates.stream()
                .map(c -> c.handle(ks))
                .filter(Objects::nonNull).findFirst()
                .orElse(null);
    }

    @Override
    protected NextState testValid() {
        return delegates.stream()
                .map(Controller::testValid)
                .filter(Objects::nonNull).findFirst()
                .orElse(null);
    }

    @Override
    protected void clear() {
        super.clear();
        delegates.forEach(Controller::clear);
    }

    @Override
    public void setup() {
        super.setup();
        delegates.forEach(this::setupDelegate);
    }
    
    protected void setupDelegate(Controller delegate) {
        delegate.setPreviousState(previousState);
        delegate.setOptions(options);
        delegate.setScreen(screen);
        delegate.setGraphics(graphics);
        delegate.setup();
    }

    @Override
    public void initialize() {
        super.initialize();
        delegates.forEach(Controller::initialize);
    }

    @Override
    protected void printDescription() {
        super.printDescription();
        delegates.forEach(Controller::printDescription);
    }

    @Override
    protected void printPrompt() {
        super.printPrompt();
        delegates.forEach(Controller::printPrompt);
    }

    @Override
    protected void printLayout() {
        super.printLayout();
        delegates.forEach(Controller::printLayout);
    }

    @Override
    protected GameState handleConfirmed(GameState state) {
        GameState first = null;
        for (Controller c : delegates) {
            GameState s = c.handleConfirmed(state);
            if (first == null) {
                first = s;
            }
        }
        return first;
    }

    @Override
    public void setPreviousState(GameState previousState) {
        delegates.forEach(c -> c.setPreviousState(previousState));
    }

}
