FROM sapmachine:17

COPY . /
RUN ./mvnw --show-version --batch-mode --no-transfer-progress dependency:copy-dependencies package

ENTRYPOINT ["/entrypoint.sh"]