/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.basepom.mojo.repack;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.BiConsumer;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedMap;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.utils.logging.MessageBuilder;
import org.apache.maven.shared.utils.logging.MessageUtils;

final class Reporter {

    private static final PluginLog LOG = new PluginLog(Reporter.class);

    private static final Set<Artifact> runtimeUnpackedArtifacts = new HashSet<>();
    private static final Set<Artifact> optionalArtifacts = new HashSet<>();
    private static final Map<Artifact, String> excludedArtifacts = new HashMap<>();
    private static final Set<Artifact> includedArtifacts = new HashSet<>();


    static void addRuntimeUnpacked(Artifact artifact) {
        runtimeUnpackedArtifacts.add(artifact);
    }

    static void addOptional(Artifact artifact) {
        optionalArtifacts.add(artifact);
    }

    static void addExcluded(Artifact artifact, String reason) {
        excludedArtifacts.put(artifact, reason);
    }

    static void addIncluded(Artifact artifact) {
        includedArtifacts.add(artifact);
    }

    public static void report(boolean quiet, Artifact source, String classifier) {

        LOG.report(quiet, "");
        header(quiet, format(Locale.ROOT, "Summary Report for: %s:%s (%s)", source.getGroupId(), source.getArtifactId(), classifier));
        LOG.report(quiet, "");

        header(quiet, "Included dependencies (" + includedArtifacts.size() + ")");
        logReport(quiet, includedArtifacts, (messageBuilder, artifact) -> messageBuilder.strong(artifact.getScope()));

        header(quiet, "Included optional dependencies (" + optionalArtifacts.size() + ")");
        logReport(quiet, optionalArtifacts, null);

        header(quiet, "Excluded dependencies (" + excludedArtifacts.size() + ")");
        logReport(quiet, excludedArtifacts.keySet(), (messageBuilder, artifact) -> messageBuilder.strong(excludedArtifacts.get(artifact)));

        header(quiet, "Runtime unpacked dependencies (" + runtimeUnpackedArtifacts.size() + ")");
        logReport(quiet, runtimeUnpackedArtifacts, null);
    }

    private static void header(boolean quiet, String value) {
        LOG.report(quiet, value);
        LOG.report(quiet, Strings.repeat("=", value.length()));
    }

    private static void logReport(boolean quiet, Set<Artifact> artifacts, BiConsumer<MessageBuilder, Artifact> consumer) {
        if (!artifacts.isEmpty()) {
            SortedMap<String, Artifact> names = computeNames(artifacts);
            final int namePadding = names.keySet().stream().map(String::length).reduce(0, Math::max);

            for (Map.Entry<String, Artifact> entry : names.entrySet()) {
                final MessageBuilder mb = MessageUtils.buffer();
                if (consumer == null) {
                    mb.a(entry.getKey());
                } else {
                    mb.a(Strings.padEnd(entry.getKey() + ':', namePadding + 2, ' '));
                    consumer.accept(mb, entry.getValue());
                }
                LOG.report(quiet, "%s", mb);
            }
        }
        LOG.report(quiet, "");
    }

    private static SortedMap<String, Artifact> computeNames(Set<Artifact> artifacts) {
        ImmutableSortedMap.Builder<String, Artifact> builder = ImmutableSortedMap.naturalOrder();
        artifacts.forEach(a -> builder.put(computeArtifactName(a), a));
        return builder.build();
    }

    private static String computeArtifactName(Artifact artifact) {
        if (artifact.hasClassifier()) {
            return artifact.getGroupId() + ':' + artifact.getArtifactId() + ':' + artifact.getClassifier();
        } else {
            return artifact.getGroupId() + ':' + artifact.getArtifactId();
        }
    }
}
